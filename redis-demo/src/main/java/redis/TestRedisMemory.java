package redis;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 测试内存占用情况
 *
 * @author wangzhengpeng
 */
public class TestRedisMemory {

    private Jedis jedis = new Jedis("127.0.0.1");

    public static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        TestRedisMemory testRedisMemory = new TestRedisMemory();

//        testRedisMemory.addStringType();
        testRedisMemory.addZSetType();
//        testRedisMemory.addSetType();
//        testRedisMemory.addHashType();
    }

    /**
     * 测试结果 -> 占用内存: 58.59M
     */
    private Future<?> addStringType() {
        return executorService.submit(() -> {
            for (int i = 0; i < 50 * 10000; i++) {
                jedis.set("你好李焕英[string]" + (i + 1), "你好李焕英");
            }
        });
    }

    /**
     * 测试结果 -> 占用内存: 134.87M
     */
    private void addSetType() {
        for (int i = 0; i < 50 * 10000; i++) {
            jedis.sadd("你好李焕英[set]:" + (i + 1), "你好李焕英");
        }
    }

    /**
     * 测试结果 -> 占用内存: 61.13M
     */
    private void addZSetType() {
        for (int i = 500000; i < 100 * 10000; i++) {
            jedis.zadd("你好李焕英[zset]:", (i + 1), "你好李焕英" + (i + 1));
        }
    }

    /**
     * 测试结果 -> 占用内存: 50.94M
     */
    private void addHashType() {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 50 * 10000; i++) {
            map.put("你好李焕英" + (i + 1), "你好李焕英");
        }
        jedis.hset("你好李焕英[hash]", map);
    }

}
