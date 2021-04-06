package com.distributed.lock.redis;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 *
 * @author wangzhengpeng
 */
public interface DistributedLock {

    /**
     * 尝试获取锁
     *
     * @param waitTime 等待尝试时间
     * @throws InterruptedException
     */
    boolean tryLock(long waitTime, TimeUnit unit) throws InterruptedException;

    /**
     * 等待加锁直到成功
     *
     * @throws InterruptedException
     */
    void lockInterruptibly() throws InterruptedException;

    /**
     * 解锁 - 强制使用 try-finally 中释放
     */
    void unlock();
}
