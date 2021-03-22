package com.distributed.lock.redis.pojo;

import com.alibaba.fastjson.JSON;

import java.util.concurrent.TimeUnit;

/**
 * 锁上线文信息
 *
 * @author wangzhengpeng
 */
public class LockContext {
    private String resources;
    private TimeUnit expireTimeUnit;
    private Long expireTime;
    private Retry retry;
    private LockResource lockResource;
    private String redisKey;

    public LockContext(String resources, TimeUnit expireTimeUnit, Long expireTime) {
        this.resources = resources;
        this.expireTimeUnit = expireTimeUnit;
        this.expireTime = expireTime;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public TimeUnit getExpireTimeUnit() {
        return expireTimeUnit;
    }

    public void setExpireTimeUnit(TimeUnit expireTimeUnit) {
        this.expireTimeUnit = expireTimeUnit;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public LockResource getLockResource() {
        return lockResource;
    }

    public void setLockResource(LockResource lockResource) {
        this.lockResource = lockResource;
    }

    public String getRedisKey() {
        return redisKey;
    }

    public void setRedisKey(String redisKey) {
        this.redisKey = redisKey;
    }

    public Retry getRetry() {
        return retry;
    }

    public void setRetry(Retry retry) {
        this.retry = retry;
    }

    public String getRedisValue() {
        return JSON.toJSONString(this.lockResource);
    }


    @Override
    public String toString() {
        return "LockContext{" +
                "resources='" + resources + '\'' +
                ", unit=" + expireTimeUnit +
                ", expireTime=" + expireTime +
                ", retry=" + retry +
                ", lockResource=" + lockResource +
                ", redisKey='" + redisKey + '\'' +
                '}';
    }
}