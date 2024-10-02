package com.jwt.auth.controller;

import com.jwt.auth.request.*;
import com.jwt.auth.service.EventProducer;
import com.jwt.auth.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwt.auth.model.User;
import com.jwt.auth.model.UserRedis;
import com.jwt.auth.response.JwtResponse;
import com.jwt.auth.service.implement.AuthServiceImplement;

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

    @Autowired
    private AuthServiceImplement authService;

    private final EventProducer eventProducer;  // Gunakan final untuk memastikan inisialisasi melalui constructor

    // Constructor dihasilkan oleh @RequiredArgsConstructor
    // Spring akan menginjeksi EventProducer ke dalam constructor ini

    @Operation(summary = "Register user and save to Redis")
    @PostMapping("/register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(schema = @Schema(implementation = User.class)))
    })
    public ResponseEntity<UserRedis> register(@RequestBody RegisterRequest registerRequest) {
        UserRedis registeredUser = authService.register(registerRequest);

        return new ResponseEntity<>(registeredUser, HttpStatus.OK);
    }

    @Operation(summary = "Generate new token when token is expired")
    @PostMapping("/refreshToken")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully", content = @Content(schema = @Schema(implementation = JwtResponse.class)))
    })
    public ResponseEntity<JwtResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        System.out.println("Received refresh token: " + refreshTokenRequest.getRefreshToken());
        JwtResponse jwtResponse = authService.refreshToken(refreshTokenRequest);

        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    @Operation(summary = "User login")
    @PostMapping("/login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User log in successfully", content = @Content(schema = @Schema(implementation = JwtResponse.class)))
    })
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.login(loginRequest);
        eventProducer.sendLoginEvent(loginRequest.getUsername());
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    @Operation(summary = "Change Role")
    @PostMapping("/changeRole")
    @ApiResponses(value = {
            @ApiResponse(responseCode  ="200", description = "Change Role successfully", content = @Content(schema = @Schema(implementation = JwtResponse.class)))
    })
    public ResponseEntity<JwtResponse> changeRole(@RequestBody ChangeRoleRequest changerolerequest) {
        JwtResponse jwtResponse = authService.changeRole(changerolerequest);
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    @Operation(summary = "Validate Token")
    @PostMapping("/validate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token is valid", content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ResponseEntity<Boolean> refresh(@RequestBody TokenRequest tokenRequest) {
        boolean status = authService.validate(tokenRequest);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
