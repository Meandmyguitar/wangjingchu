package com.distributed.lock.redis.pojo;

/**
 * 锁信息
 *
 * @author wangzhengpeng
 */
public class LockResource {
    /**
     * 锁资源
     */
    private String resource;
    /**
     * 截止时间（毫秒）
     */
    private Long expireTimeMs;
    /**
     * 锁id
     */
    private String lockId;

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Long getExpireTimeMs() {
        return expireTimeMs;
    }

    public void setExpireTimeMs(Long expireTimeMs) {
        this.expireTimeMs = expireTimeMs;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    @Override
    public String toString() {
        return "LockResource{" +
                "resource='" + resource + '\'' +
                ", expireTimeMs=" + expireTimeMs +
                ", lockId='" + lockId + '\'' +
                '}';
    }
}