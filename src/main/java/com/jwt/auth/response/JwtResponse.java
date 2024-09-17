package com.jwt.auth.response;

import com.jwt.auth.model.Role;
import lombok.Data;

@Data
public class JwtResponse {

    private String token;
    private String refreshToken;
    private String message;
    private Role role;
}
