package com.jwt.auth.response;

import lombok.Data;

@Data
public class JwtResponse {

    private String token;
    private String refreshToken;
}
