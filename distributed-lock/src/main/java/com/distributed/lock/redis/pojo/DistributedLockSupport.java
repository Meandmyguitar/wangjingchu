package com.distributed.lock.redis.pojo;

import com.alibaba.fastjson.JSON;
import com.peppa.file.distribute.RedisDistributedLockManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 支撑类
 *
 * @author wangzhengpeng
 */
@Slf4j
public class DistributedLockSupport {

    @Autowired
    protected StringRedisTemplate stringRedisTemplate;
    /**
     * 其他线程尝试次数
     */
    protected Integer otherThreadRetryCount = 3;
    /**
     * 随机睡眠最小/最大时间（毫秒）
     */
    protected final Integer randomMinTime = 100;
    protected final Integer randomMaxTime = 200;
    /**
     * 默认时间单位（秒）
     */
    protected TimeUnit defaultTimeUnit = TimeUnit.SECONDS;
    /**
     * 锁持有时间，默认100s
     */
    protected Long defaultHolderTime = 60L;
    /**
     * 锁到期前多久开始续命(默认为持有时间过半的时候开始续命)
     */
    protected Long beforeExpireTime = defaultHolderTime / 2;
    /**
     * 续命队列（使用延迟队列）
     */
    protected DelayQueue lockResultDelayQueue = new DelayQueue<>();
    /**
     * lockId->LockContext
     */
    protected ConcurrentHashMap lockIdResultLockContextMap = new ConcurrentHashMap<>();
    /**
     * lockId->LockResult
     */
    protected ConcurrentHashMap lockIdLockResultMap = new ConcurrentHashMap<>();
    protected ReentrantLock lock = new ReentrantLock();

    /**
     * 构建分布式锁上下文
     */
    protected LockContext buildLockContext(String lockId, String resources, TimeUnit unit, Long expireTime) {
        LockContext lockContext = new LockContext(resources, unit, expireTime);
        lockContext.setRetry(Retry.create(this.otherThreadRetryCount, 0));
        lockContext.setLockResource(this.buildLockResource(lockId, resources, unit, expireTime));
        lockContext.setRedisKey(this.getRedisKey(resources));
        return lockContext;
    }

    /**
     * 构建锁资源信息
     */
    protected LockResource buildLockResource(String lockId, String resources, TimeUnit unit, Long expireTime) {
        LockResource lockResource = new LockResource();
        lockResource.setResource(resources);
        lockResource.setExpireTimeMs(System.currentTimeMillis() + unit.toMillis(expireTime));
        lockResource.setLockId(lockId);
        return lockResource;
    }

    /**
     * 保存分布式结果 and 上下文信息
     */
    protected void saveDistributedLockResultAndContextMap(LockResult lockResult, LockContext lockContext) {
        if (lockResult.isSuccess() && lockResult != null && lockContext != null) {
            lock.lock();
            try {
                this.lockIdResultLockContextMap.put(lockResult.getLockId(), lockContext);
                this.lockIdLockResultMap.put(lockResult.getLockId(), lockResult);
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 删除分布式结果 and 上下文信息
     */
    protected void removeDistributedLockResultAndContextMap(String lockId) {
        lock.lock();
        try {
            this.lockIdLockResultMap.remove(lockId);
            this.lockIdResultLockContextMap.remove(lockId);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 加入续命队列，续命队列会在任务过期前进行续命
     */
    protected void addExtendingLifeQueue(LockResult lockResult, LockContext lockContext) {
        if (lockResult.isSuccess() && lockResult != null && lockContext != null) {
            DelayedLockResult delayedLockResult = new DelayedLockResult(
                    lockResult.getLockId(),
                    lockContext.getLockResource().getExpireTimeMs(),
                    this.defaultTimeUnit.toMillis(this.beforeExpireTime));
            lockResultDelayQueue.put(delayedLockResult);
        }
    }

    protected LockResource getLockResourceFromRedis(String resources) {
        String redisKey = getRedisKey(resources);
        String redisValue = this.stringRedisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isNotBlank(redisValue)) {
            return JSON.parseObject(redisValue, LockResource.class);
        } else {
            return null;
        }
    }

    protected String getRedisKey(String resources) {
        String key = String.format("%s-%s", RedisDistributedLockManager.class.getName(), resources);
        if (log.isInfoEnabled()) {
            log.info("redis key:[{}]", key);
        }
        return key;
    }

    /**
     * 随机睡眠时间
     *
     * @param min 最小时间
     * @param max 最大时间
     * @throws InterruptedException 异常
     */
    protected static void randomSleep(int min, int max) throws InterruptedException {
        int i = new Random().nextInt(max - min);
        Thread.sleep(min + i);
    }

}
