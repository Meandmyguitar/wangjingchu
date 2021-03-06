package com.distributed.lock.redis.pojo;

/**
 * 重试
 *
 * @author wangzhengpeng
 */
public class Retry {
    /**
     * 最大重试次数
     */
    private int maxCount;
    /**
     * 已重试次数
     */
    private int count;

    public Retry(int maxCount, int count) {
        this.maxCount = maxCount;
        this.count = count;
    }

    public static Retry create(int maxCount, int count) {
        return new Retry(maxCount, count);
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Retry{" +
                "maxCount=" + maxCount +
                ", count=" + count +
                '}';
    }
}