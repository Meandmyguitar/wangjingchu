package com.distributed.lock.redis.pojo;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayedLockResult implements Delayed {
    /**
     * 锁id
     */
    private String lockId;
    /**
     * 锁有效期(ms)
     */
    private long expireTimeMs;
    /**
     * 多久续命一次(ms)
     */
    private long beforeExpireTime;

    /**
     * @param lockId           锁id
     * @param expireTimeMs     锁有效期（ms）
     * @param beforeExpireTime 锁到期前多久开始续命
     */
    public DelayedLockResult(String lockId, long expireTimeMs, long beforeExpireTime) {
        this.lockId = lockId;
        this.expireTimeMs = expireTimeMs;
        this.beforeExpireTime = beforeExpireTime;
    }

    public String getLockId() {
        return lockId;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expireTimeMs - this.beforeExpireTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        DelayedLockResult o2 = (DelayedLockResult) o;
        return Long.compare(this.expireTimeMs, o2.expireTimeMs);
    }

    @Override
    public String toString() {
        return "DelayedLockResult{" +
                "lockId='" + lockId + '\'' +
                ", expireTimeMs=" + expireTimeMs +
                ", beforeExpireTime=" + beforeExpireTime +
                '}';
    }
}