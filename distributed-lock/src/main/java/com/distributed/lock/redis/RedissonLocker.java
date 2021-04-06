package com.distributed.lock.redis;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * redisson 分布式锁
 *
 * @author wangzhengpeng
 * @see #newLock(String) 创建分布式锁[对象]
 * @see Lock#tryLock(long, TimeUnit) 尝试获取分布式锁...等待给定时间
 * @see Lock#lockInterruptibly() 尝试获取分布式锁...直到成功
 * @see Lock#unlock() 释放分布式锁
 */
@Component
public class RedissonLocker implements AutoCloseable {

    private final RedissonClient client;

    private String prefix = "##rlock##";

    public RedissonLocker(RedissonClient client) {
        this.client = client;
    }

    /**
     * 创建分布式锁
     *
     * @param name 锁名称-业务保证唯一可用性
     * @return 分布式锁对象
     */
    public DistributedLock newLock(String name) {
        return new Lock(client.getLock(prefix + name));
    }

    @Override
    public void close() {
        client.shutdown();
    }

    class Lock implements DistributedLock {

        private RLock lock;

        Lock(RLock lock) {
            this.lock = lock;
        }

        @Override
        public boolean tryLock(long waitTime, TimeUnit unit) throws InterruptedException {
            return lock.tryLock(waitTime, -1, unit);
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            lock.lockInterruptibly(-1, TimeUnit.SECONDS);
        }

        @Override
        public void unlock() {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
