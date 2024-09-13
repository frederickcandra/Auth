package com.jwt.auth.service;

import com.jwt.auth.model.UserRedis;

public interface RedisService {

    void saveUser(String userId, UserRedis userRedis);

    UserRedis getUser(String userId);

    void saveToken(String token, String userId);

    String getUserIdFromToken(String token);
}