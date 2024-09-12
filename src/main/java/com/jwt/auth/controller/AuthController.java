package com.jwt.auth.controller;

import com.jwt.auth.service.UserService;
import com.jwt.auth.service.implement.AuthServiceImplement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jwt.auth.model.User;
import com.jwt.auth.request.LoginRequest;
import com.jwt.auth.request.RefreshTokenRequest;
import com.jwt.auth.request.RegisterRequest;
import com.jwt.auth.response.JwtResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "Operations to manage users authentication")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);  // Tambahkan logger

    @Autowired
    private AuthServiceImplement authService;

    @Operation(summary = "Register user and save to Redis")
    @PostMapping("/register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(schema = @Schema(implementation = User.class)))
    })
    public ResponseEntity<User> register(@RequestBody RegisterRequest registerRequest) {
        logger.info("Received registration request for user: {}", registerRequest.getUsername());
        User registeredUser = authService.register(registerRequest);
        logger.info("User registered successfully: {}", registeredUser.getUsername());
        return new ResponseEntity<>(registeredUser, HttpStatus.OK);
    }

    @Operation(summary = "Generate new token when token is expired")
    @PostMapping("/refreshToken")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully", content = @Content(schema = @Schema(implementation = JwtResponse.class)))
    })
    public ResponseEntity<JwtResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        logger.info("Refreshing token for user...");
        JwtResponse jwtResponse = authService.refreshToken(refreshTokenRequest);
        logger.info("Token refreshed successfully.");
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        logger.info("Received login request for user: {}", loginRequest.getUsername());
        JwtResponse jwtResponse = authService.login(loginRequest);
        logger.info("Login successful for user: {}", loginRequest.getUsername());
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }
}
