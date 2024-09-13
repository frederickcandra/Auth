package com.jwt.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.HashOperations;

import com.jwt.auth.model.UserRedis;
import com.jwt.auth.service.implement.RedisServiceImplement;

class RedisServiceImplementTest {

    @Mock
    private RedisTemplate<String, UserRedis> redisTemplate;

    @Mock
    private ValueOperations<String, UserRedis> valueOperations;

    @Mock
    private HashOperations<String, String, String> hashOperations;  // Mock HashOperations

    @InjectMocks
    private RedisServiceImplement redisService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock opsForValue and opsForHash to return the correct operations
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
//        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    void testSaveUser() {
        String userId = "testUserId";
        UserRedis userRedis = new UserRedis();
        userRedis.setUsername("testUser");
        userRedis.setPassword("testPassword");

        redisService.saveUser(userId, userRedis);

        verify(valueOperations, times(1)).set(userId, userRedis);
    }

    @Test
    void testGetUser() {
        String userId = "testUserId";
        UserRedis userRedis = new UserRedis();
        userRedis.setUsername("testUser");
        userRedis.setPassword("testPassword");

        when(valueOperations.get(userId)).thenReturn(userRedis);

        UserRedis result = redisService.getUser(userId);

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals("testPassword", result.getPassword());
        verify(valueOperations, times(1)).get(userId);
    }

    @Test
    void testSaveToken() {
        String userId = "testUserId";
        String token = "testToken";
        UserRedis userRedis = new UserRedis();
        userRedis.setUsername("testUser");

        when(valueOperations.get(userId)).thenReturn(userRedis);

        redisService.saveToken(token, userId);

        assertEquals(token, userRedis.getToken());
        verify(valueOperations, times(1)).set(userId, userRedis);
    }

//    @Test
//    void testGetUserIdFromToken() {
//        String token = "testToken";
//        String expectedUserId = "testUserId";
//
//        // Mock the behavior for hash operations (here the key is "TOKEN" and token)
//        when(hashOperations.get("TOKEN", token)).thenReturn(expectedUserId);
//
//        String result = redisService.getUserIdFromToken(token);
//
//        assertEquals(expectedUserId, result);
//        verify(hashOperations, times(1)).get("TOKEN", token);
//    }
}
