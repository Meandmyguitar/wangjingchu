package com.distributed.lock.redis;


import com.distributed.lock.redis.pojo.LockResult;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 * @author wangzhengpeng
 */
public interface RedisDistributedLock {

    /**
     * 获取分布式锁，会立即返回
     *
     * @param resources 资源
     * @return {@link LockResult}
     */
    LockResult lock(String resources);

    /**
     * 尝试获取分布式锁（支持超时时间）
     *
     * @param resources 资源
     * @param timeout   获取锁等待时间
     * @param unit      timeout时间单位
     * @return 上锁结果 {@link LockResult}
     */
    LockResult tryLock(String resources, long timeout, TimeUnit unit);

    /**
     * 释放锁
     *
     * @param lockId {@link LockResult#getLockId()}的返回值
     */
    void unLock(String lockId);


}