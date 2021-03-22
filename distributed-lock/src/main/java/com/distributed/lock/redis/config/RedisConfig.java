package com.distributed.lock.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

/**
 * @author wangzhengpeng
 */
@Component
public class RedisConfig {

    @Bean(name = "redissonRed1")
    @Primary
    public RedissonClient redissonRed1() {
        Config config = new Config();
        config.useSingleServer().setAddress("127.0.0.1:6379").setDatabase(0);
        return Redisson.create(config);
    }

    @Bean(name = "redissonRed2")
    public RedissonClient redissonRed2() {
        Config config = new Config();
        config.useSingleServer().setAddress("127.0.0.1:6380").setDatabase(0);
        return Redisson.create(config);
    }

    @Bean(name = "redissonRed3")
    public RedissonClient redissonRed3() {
        Config config = new Config();
        config.useSingleServer().setAddress("127.0.0.1:6381").setDatabase(0);
        return Redisson.create(config);
    }

    @Bean(name = "redissonRed4")
    public RedissonClient redissonRed4() {
        Config config = new Config();
        config.useSingleServer().setAddress("127.0.0.1:6382").setDatabase(0);
        return Redisson.create(config);
    }

    @Bean(name = "redissonRed5")
    public RedissonClient redissonRed5() {
        Config config = new Config();
        config.useSingleServer().setAddress("127.0.0.1:6383").setDatabase(0);
        return Redisson.create(config);
    }


    /**
     * 单个redis
     */
    @Bean
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    /**
     * 单个redisson
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("127.0.0.1:6379").setDatabase(0);
        return Redisson.create(config);
    }


    @Bean
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        return new RedisLockRegistry(redisConnectionFactory, "my_lock");
    }

}
