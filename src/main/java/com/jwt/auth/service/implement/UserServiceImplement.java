package com.jwt.auth.service.implement;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jwt.auth.model.User;
import com.jwt.auth.service.UserService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImplement implements UserService {

    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, User> hashOperations;

    @PostConstruct
    private void init() {
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void saveUser(User user) {
        hashOperations.put("USER", user.getUsername(), user);
    }

    @Override
    public User findUserByUsername(String username) {
        User user = hashOperations.get("USER", username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findUserByUsername(username);
    }
}
