package com.jpms.codinggame.service;

import com.jpms.codinggame.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class RedisService {

    @Autowired
    @Qualifier(value = "mainRedisTemplate")
    private RedisTemplate<String,Object> redisTemplate;

    private HashOperations<String,String,Object> hashOps;

    /*
    * HASH 관련 함수
    * */
    @Autowired
    public void setHashOperations(){
        this.hashOps = redisTemplate.opsForHash();
    }

    public void put(String key, String hashKey, Object value) {
        hashOps.put(key, hashKey, value);
    }

    public Object get(String key, String hashKey) {
        return hashOps.get(key, hashKey);
    }

    public Map<String, Object> getAll(String key) {
        return hashOps.entries(key);
    }

    public void delete(String key) { redisTemplate.delete(key); }

    public void delete(String key, String... hashKeys) {
        hashOps.delete(key, (Object[]) hashKeys);
    }

    public boolean hasKey(String key, String hashKey) {
        return hashOps.hasKey(key, hashKey);
    }

    public Object getPossibleCount(User user) {return hashOps.get(String.valueOf(user.getId()),"possibleCount");}

    public Object getTodayScore(User user) {return hashOps.get(String.valueOf(user.getId()),"score");}

    public boolean isEmptyKey(String key) { return hashOps.values(key).isEmpty(); }
    /*
    * Key 함수
    * */
    public Set<String> getAllKeys(){
        return redisTemplate.keys("*");
    }

}
