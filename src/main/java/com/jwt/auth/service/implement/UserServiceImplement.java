package com.jwt.auth.service.implement;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jwt.auth.model.User;
import com.jwt.auth.model.UserRedis;
import com.jwt.auth.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImplement implements UserService {

    private final RedisTemplate<String, UserRedis> redisTemplate;

    @Override
    public void saveUser(User user) {
        // Convert User to UserRedis
        UserRedis userRedis = new UserRedis();
        userRedis.setUsername(user.getUsername());
        userRedis.setPassword(user.getPassword());
        userRedis.setRole(user.getRole());

        redisTemplate.opsForValue().set(user.getUsername(), userRedis);
    }

    @Override
    public User findUserByUsername(String username) {
        // Fetch UserRedis from Redis
        UserRedis userRedis = redisTemplate.opsForValue().get(username);
        if (userRedis == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Convert UserRedis to User
        User user = new User();
        user.setUsername(userRedis.getUsername());
        user.setPassword(userRedis.getPassword());
        user.setRole(userRedis.getRole());

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findUserByUsername(username);
    }
}