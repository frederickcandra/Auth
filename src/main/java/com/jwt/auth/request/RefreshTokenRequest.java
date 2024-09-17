package com.jwt.auth.request;

import com.jwt.auth.model.User;
import com.jwt.auth.model.UserRedis;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String username;
    private String refreshToken;
}
