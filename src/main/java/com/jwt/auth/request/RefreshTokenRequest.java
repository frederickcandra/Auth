package com.jwt.auth.request;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String token;
}
