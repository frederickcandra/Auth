package com.jwt.auth.service.implement;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.jwt.auth.model.User;
import com.jwt.auth.service.RedisService;

@Service
public class RedisServiceImplement implements RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveUser(String userId, User user) {
        redisTemplate.opsForHash().put("USER", userId, user);
        redisTemplate.expire("USER", 1, TimeUnit.DAYS);
    }

    @Override
    public User getUser(String userId) {

        return (User) redisTemplate.opsForHash().get("USER", userId);
    }

    @Override
    public void saveToken(String token, String userId) {
        redisTemplate.opsForHash().put("TOKEN", token, userId);
        redisTemplate.expire("TOKEN", 1, TimeUnit.DAYS);
    }

    @Override
    public String getUserIdFromToken(String token) {
        return (String) redisTemplate.opsForHash().get("TOKEN", token);
    }
}
