package com.distributed.lock.redis;

import com.distributed.lock.redis.pojo.DistributedLockSupport;
import com.distributed.lock.redis.pojo.LockContext;
import com.distributed.lock.redis.pojo.LockResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * @author wangzhengpeng
 */
@Component
public class RedisDistributedLockManager extends DistributedLockSupport implements RedisDistributedLock, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(RedisDistributedLockManager.class);

    private final MyWatchDog myWatchDog;

    public RedisDistributedLockManager(MyWatchDog myWatchDog) {
        this.myWatchDog = myWatchDog;
    }

    @Override
    public LockResult lock(String resources) {
        lock.lock();
        try {
            LockContext lockContext = this.buildLockContext(UUID.randomUUID().toString(), resources, defaultTimeUnit, defaultHolderTime);
            boolean success = this.lockIn(lockContext);
            LockResult lockResult = new LockResult(success, lockContext.getLockResource().getLockId(), resources);
            //Join local cache
            this.saveDistributedLockResultAndContextMap(lockResult, lockContext);
            //Join renewal life queue
            this.addExtendingLifeQueue(lockResult, lockContext);
            return lockResult;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public LockResult tryLock(String resources, long timeout, TimeUnit unit) {
        long expireTimeMs = System.currentTimeMillis() + unit.toMillis(timeout);
        while (true) {
            LockResult lockResult = lock(resources);
            if (lockResult.isSuccess() || expireTimeMs < System.currentTimeMillis()) {
                return lockResult;
            } else {
                try {
                    randomSleep(this.randomMinTime, this.randomMaxTime);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                    return lockResult;
                }
            }
        }
    }

    @Override
    public void unLock(String lockId) {
        if (!this.lockIdLockResultMap.containsKey(lockId)) {
            logger.info("distributed lock un exists ：[{}]", lockId);
            return;
        }
        lock.lock();
        try {
            LockResult lockResult = (LockResult) this.lockIdLockResultMap.get(lockId);
            if (lockResult == null) {
                logger.info("distributed lock un exists ：[{}]", lockId);
            }
            //delete local-map
            this.removeDistributedLockResultAndContextMap(lockId);
            //delete redis
            if (Objects.isNull(lockResult) || StringUtils.isAllBlank(lockResult.getResources(), lockResult.getLockId())) {
                logger.error("lock result error : lockResult={}", lockResult);
                return;
            }
            LockResource lockResource = this.getLockResourceFromRedis(lockResult.getResources());
            if (lockResource != null && lockResult.getLockId().equals(lockResource.getLockId())) {
                this.stringRedisTemplate.delete(this.getRedisKey(lockResult.getResources()));
                logger.info("distributed lock released successfully ：[{}]", lockResult);
            } else {
                logger.info("distributed has been released：[{}]", lockResult);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * lock-in
     */
    private boolean lockIn(LockContext lockContext) {
        boolean result = this.stringRedisTemplate.opsForValue().setIfAbsent(lockContext.getRedisKey(), lockContext.getRedisValue());
        if (result) {
            // todo Call non atomic here
            this.stringRedisTemplate.expire(lockContext.getRedisKey(), lockContext.getExpireTime(), lockContext.getExpireTimeUnit());
        } else {
            //lock timeout,un release,other users try
            result = this.othersGetLock(lockContext);
        }
        return result;
    }

    /**
     * lock timeout,un release,other users try
     */
    private boolean othersGetLock(LockContext lockContext) {
        if (lockContext == null) {
            return false;
        }
        LockResource lockResource = this.getLockResourceFromRedis(lockContext.getResources());
        if (null == lockResource) {
            return false;
        }
        // If the lock is valid and has not been released,other users try acquire.
        // Here,expiration date is added with a random number to reduce the probability of collision among multiple acquisitions
        // resulting in the successful acquisition of the lock
        if (lockResource.getExpireTimeMs() + new Random().nextInt(5000) < System.currentTimeMillis()) {
            String redisKey = this.getRedisKey(lockResource.getResource());
            this.stringRedisTemplate.delete(redisKey);
            if (lockContext.getRetry().getMaxCount() > lockContext.getRetry().getCount()) {
                lockContext.getRetry().setCount(lockContext.getRetry().getCount() + 1);
                return this.lockIn(lockContext);
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public void afterPropertiesSet() {
        // start renewal life thread
        Thread extendingLifeThread = new Thread(myWatchDog.executionRenewal());
        extendingLifeThread.setDaemon(true);
        extendingLifeThread.start();
    }
}