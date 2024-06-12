package com.jpms.codinggame.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SubRedisService {
    @Autowired
    @Qualifier(value = "subRedisTemplate")
    private RedisTemplate<String,String> redisTemplate;

    //Value 지정 시 TTL 5분으로 설정해놨음
    public void setValue(String key, String value){redisTemplate.opsForValue().set(key,value,1, TimeUnit.MINUTES);}
    public String getValue(String key){return redisTemplate.opsForValue().get(key);}
}
