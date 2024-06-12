package com.jpms.codinggame.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class MainRedisConfig extends RedisConfig {

    @Bean
    @Primary
    public RedisConnectionFactory mainRedisConnectionFactory() {
        return redisConnectionFactory(0);
    }

    @Bean(name = "mainRedisTemplate")
    @Qualifier(value = "mainRedisTemplate")
    public RedisTemplate<String, Object> redisTemplate(){
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.setConnectionFactory(mainRedisConnectionFactory());
        return redisTemplate;
    }
}
