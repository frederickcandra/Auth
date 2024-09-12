package com.jwt.auth.request;
import lombok.Data;

@Data
public class TokenRequest {
    private String username;
    private String token;
}
