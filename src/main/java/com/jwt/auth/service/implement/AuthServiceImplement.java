package com.jwt.auth.service.implement;

import java.util.HashMap;

import com.jwt.auth.request.ChangeRoleRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.jwt.auth.model.Role;
import com.jwt.auth.model.User;
import com.jwt.auth.model.UserRedis;
import com.jwt.auth.request.LoginRequest;
import com.jwt.auth.request.RefreshTokenRequest;
import com.jwt.auth.request.RegisterRequest;
import com.jwt.auth.response.JwtResponse;
import com.jwt.auth.service.AuthService;
import com.jwt.auth.service.JwtService;
import com.jwt.auth.service.RedisService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImplement implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RedisService redisService;

    @Override
    public UserRedis register(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER);

        UserRedis userRedis = new UserRedis();
        userRedis.setUsername(user.getUsername());
        userRedis.setPassword(user.getPassword());
        userRedis.setRole(user.getRole());

        redisService.saveUser(user.getUsername(), userRedis);

        return userRedis;
    }

    @Override
    public JwtResponse login(LoginRequest loginRequest) {
        // Fetch the UserRedis object from Redis
        UserRedis userRedis = redisService.getUser(loginRequest.getUsername());
        if (userRedis == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        // Manually map fields from UserRedis to User for authentication
        User user = new User();
        user.setUsername(userRedis.getUsername());
        user.setPassword(userRedis.getPassword());
        user.setRole(userRedis.getRole());

        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        // Authenticate the user
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        // Generate tokens
        var token = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        // Save the token in Redis
        redisService.saveToken(token, user.getUsername());

        // Return the JWT response
        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setToken(token);
        jwtResponse.setRefreshToken(refreshToken);

        return jwtResponse;
    }

    @Override
    public JwtResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        // Extract the username from the refresh token
        String username = jwtService.extractUsername(refreshTokenRequest.getToken());

        // Fetch the UserRedis object from Redis
        UserRedis userRedis = redisService.getUser(username);
        if (userRedis == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        // Manually map fields from UserRedis to User for token validation
        User user = new User();
        user.setUsername(userRedis.getUsername());
        user.setPassword(userRedis.getPassword());
        user.setRole(userRedis.getRole());

        // Validate the token
        if (jwtService.isTokenValid(refreshTokenRequest.getToken(), user)) {
            // Generate a new token
            var token = jwtService.generateToken(user);

            // Create and return the JwtResponse with the new token and existing refresh
            // token
            JwtResponse jwtResponse = new JwtResponse();
            jwtResponse.setToken(token);
            jwtResponse.setRefreshToken(refreshTokenRequest.getToken());

            return jwtResponse;
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
    }

    @Override

    public JwtResponse changeRole(ChangeRoleRequest changeRoleRequest) {
            // Fetch the UserRedis object based on username

            UserRedis userRedis = redisService.getUser(changeRoleRequest.getUsername());
            if (userRedis == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
            }

            // Update the role in the UserRedis object
            userRedis.setRole(Role.ADMIN);

            // Save the updated user back to Redis
            redisService.saveUser(userRedis.getUsername(), userRedis);

            // Return a successful response
            JwtResponse jwtResponse = new JwtResponse();
            jwtResponse.setSetMessage("Success Change Role");
            jwtResponse.setRole(Role.ADMIN);
            return jwtResponse;
        }
    }
