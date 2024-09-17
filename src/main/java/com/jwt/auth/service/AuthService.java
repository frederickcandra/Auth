package com.jwt.auth.service;

import com.jwt.auth.model.UserRedis;
import com.jwt.auth.request.ChangeRoleRequest;
import com.jwt.auth.request.LoginRequest;
import com.jwt.auth.request.RefreshTokenRequest;
import com.jwt.auth.request.RegisterRequest;
import com.jwt.auth.response.JwtResponse;

public interface AuthService {

    UserRedis register(RegisterRequest registerRequest);

    JwtResponse login(LoginRequest loginRequest);

    JwtResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    JwtResponse changeRole(ChangeRoleRequest changeRoleRequest);
}
