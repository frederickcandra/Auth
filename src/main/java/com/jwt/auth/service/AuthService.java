package com.jwt.auth.service;

import com.jwt.auth.model.User;
import com.jwt.auth.request.LoginRequest;
import com.jwt.auth.request.RefreshTokenRequest;
import com.jwt.auth.request.RegisterRequest;
import com.jwt.auth.response.JwtResponse;

public interface AuthService {
    User register(RegisterRequest registerRequest);

    JwtResponse login(LoginRequest loginRequest);

    JwtResponse refreshToken(RefreshTokenRequest refreshTokenRequest);


}
