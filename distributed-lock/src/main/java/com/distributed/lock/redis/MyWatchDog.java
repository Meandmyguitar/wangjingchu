package com.distributed.lock.redis;

import com.distributed.lock.redis.pojo.DistributedLockSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class MyWatchDog extends DistributedLockSupport {

    @Autowired
    private RedisDistributedLock redisDistributedLock;

    public static final Logger logger = LoggerFactory.getLogger(MyWatchDog.class);

    /**
     * Execution renewal
     */
    public Runnable executionRenewal() {
        return () -> {
            while (true) {
                DelayedLockResult delayedLockResult = null;
                try {
                    delayedLockResult = (DelayedLockResult) lockResultDelayQueue.poll(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
                if (delayedLockResult == null) {
                    break;
                }
                logger.info("renewal life start ：[{}]", delayedLockResult);
                //是否在当前锁列表中
                String lockId = delayedLockResult.getLockId();
                LockResult lockResult = (LockResult) lockIdLockResultMap.get(lockId);
                LockContext lockContext = (LockContext) lockIdResultLockContextMap.get(lockId);
                logger.info("lockResult:[{}],lockContext:[{}]", lockResult, lockContext);
                if (lockResult != null && lockContext != null) {
                    logger.info("before:" + lockContext.getLockResource().getExpireTimeMs() + " " + (lockContext.getLockResource().getExpireTimeMs() - System.currentTimeMillis()));
                    boolean extendingLifeResult = renewalLife(lockId, defaultTimeUnit, defaultHolderTime);
                    lockResult = (LockResult) lockIdLockResultMap.get(lockId);
                    lockContext = (LockContext) lockIdResultLockContextMap.get(lockId);
                    logger.info("renewal life success：[{}]", extendingLifeResult);
                    //若续命成功，则继续加入续命队列
                    if (extendingLifeResult && lockResult != null && lockContext != null) {
                        logger.info(" after:" + lockContext.getLockResource().getExpireTimeMs() + " " + (lockContext.getLockResource().getExpireTimeMs() - System.currentTimeMillis()));
                        saveDistributedLockResultAndContextMap(lockResult, lockContext);
                        // Join renewal life queue
                        addExtendingLifeQueue(lockResult, lockContext);
                    } else {
                        logger.info("renewal life fail,ready release lock,lockId={}", lockId);
                        redisDistributedLock.unLock(lockId);
                    }
                }
                logger.info("renewal life end ：[{}]", delayedLockResult);
            }
        };
    }


    /**
     * renewal life
     */
    private boolean renewalLife(String lockId, TimeUnit unit, Long time) {
        lock.lock();
        try {
            LockResult lockResult = (LockResult) lockIdLockResultMap.get(lockId);
            if (lockResult == null) {
                return false;
            }
            //重新构造LockContext
            LockContext lockContext = buildLockContext(lockId, lockResult.getResources(), unit, time);
            //从redis中获取锁信息
            LockResource lockResource = getLockResourceFromRedis(lockResult.getResources());
            //lockResource!=null && 锁未超时 && redis中的lockId于当前线程中的lockId一致
            long currentTimeMillis = System.currentTimeMillis();
            if (lockResource != null &&
                    lockResource.getExpireTimeMs() > currentTimeMillis &&
                    lockResource.getLockId().equals(lockResult.getLockId())) {
                stringRedisTemplate.opsForValue().set(
                        lockContext.getRedisKey(),
                        lockContext.getRedisValue(),
                        lockContext.getExpireTime(),
                        lockContext.getExpireTimeUnit());
                saveDistributedLockResultAndContextMap(lockResult, lockContext);
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }
}
