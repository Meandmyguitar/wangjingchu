package com.distributed.lock.redis.pojo;

import io.swagger.annotations.ApiModelProperty;

/**
 * 上锁结果
 */
public class LockResult {

    @ApiModelProperty(value = "上锁是否成功")
    private boolean success;

    @ApiModelProperty(value = "锁id")
    private String lockId;

    @ApiModelProperty(value = "资源")
    private String resources;

    public LockResult(boolean success, String lockId, String resources) {
        this.success = success;
        this.lockId = lockId;
        this.resources = resources;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    @Override
    public String toString() {
        return "LockResult{" +
                "resources='" + resources + '\'' +
                ", success=" + success +
                ", lockId='" + lockId + '\'' +
                '}';
    }
}