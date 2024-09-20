package com.jwt.auth.service;

import com.jwt.auth.model.UserRedis;
import com.jwt.auth.request.*;
import com.jwt.auth.response.JwtResponse;

public interface AuthService {

    UserRedis register(RegisterRequest registerRequest);

    JwtResponse login(LoginRequest loginRequest);

    JwtResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    JwtResponse changeRole(ChangeRoleRequest changeRoleRequest);

    boolean validate(TokenRequest tokenRequest);

}
