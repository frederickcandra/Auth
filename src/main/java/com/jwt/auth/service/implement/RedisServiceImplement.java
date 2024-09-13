package com.jwt.auth.service.implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.jwt.auth.model.UserRedis;
import com.jwt.auth.service.RedisService;

@Service
public class RedisServiceImplement implements RedisService {

    @Autowired
    private RedisTemplate<String, UserRedis> redisTemplate;

    @Override
    public void saveUser(String userId, UserRedis userRedis) {
        redisTemplate.opsForValue().set(userId, userRedis);
    }

    @Override
    public UserRedis getUser(String userId) {
        return redisTemplate.opsForValue().get(userId);
    }

    @Override
    public void saveToken(String token, String userId) {
        UserRedis userRedis = redisTemplate.opsForValue().get(userId);
        userRedis.setToken(token);
        redisTemplate.opsForValue().set(userId, userRedis);
    }

    @Override
    public String getUserIdFromToken(String token) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        if (hashOperations == null) {
            throw new IllegalStateException("HashOperations not initialized");
        }
        return hashOperations.get("TOKEN", token);
    }
}