package com.jpms.codinggame.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class SubRedisConfig extends RedisConfig{
    @Bean
    public RedisConnectionFactory subRedisConnectionFactory() {
        return redisConnectionFactory(1);
    }

    @Bean(name="subRedisTemplate")
    @Qualifier(value = "subRedisTemplate")
    public RedisTemplate<String, String> redisTemplate(){
        RedisTemplate<String,String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.setConnectionFactory(subRedisConnectionFactory());
        return redisTemplate;
    }
}
