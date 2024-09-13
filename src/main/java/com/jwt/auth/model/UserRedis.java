package com.jwt.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRedis {

    private String username;
    private String password;
    private String token;
    private Role role;
}