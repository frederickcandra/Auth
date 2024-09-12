package com.jwt.auth.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.jwt.auth.model.User;
public interface UserService extends UserDetailsService {

    void saveUser(User user);

    User findUserByUsername(String username);
}
