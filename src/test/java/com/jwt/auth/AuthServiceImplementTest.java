package com.jwt.auth;

import com.jwt.auth.model.Role;
import com.jwt.auth.model.User;
import com.jwt.auth.model.UserRedis;
import com.jwt.auth.request.LoginRequest;
import com.jwt.auth.request.RegisterRequest;
import com.jwt.auth.response.JwtResponse;
import com.jwt.auth.service.JwtService;
import com.jwt.auth.service.RedisService;
import com.jwt.auth.service.implement.AuthServiceImplement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceImplementTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private AuthServiceImplement authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testUser");
        registerRequest.setPassword("testPassword");

        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");

        UserRedis savedUser = authService.register(registerRequest);

        assertNotNull(savedUser);
        assertEquals("testUser", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(Role.USER, savedUser.getRole());

        verify(redisService, times(1)).saveUser(eq("testUser"), any(UserRedis.class));
    }

    @Test
    void testLoginSuccess() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("testPassword");

        UserRedis userRedis = new UserRedis();
        userRedis.setUsername("testUser");
        userRedis.setPassword("encodedPassword");
        userRedis.setRole(Role.USER);

        when(redisService.getUser("testUser")).thenReturn(userRedis);
        when(passwordEncoder.matches(loginRequest.getPassword(), userRedis.getPassword())).thenReturn(true);
        when(jwtService.generateToken(any(User.class))).thenReturn("token");
        when(jwtService.generateRefreshToken(anyMap(), any(User.class))).thenReturn("refreshToken");

        JwtResponse jwtResponse = authService.login(loginRequest);

        assertNotNull(jwtResponse);
        assertEquals("token", jwtResponse.getToken());
        assertEquals("refreshToken", jwtResponse.getRefreshToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(redisService, times(1)).saveToken("token", "testUser");
    }

    @Test
    void testLoginInvalidCredentials() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("wrongPassword");

        UserRedis userRedis = new UserRedis();
        userRedis.setUsername("testUser");
        userRedis.setPassword("encodedPassword");
        userRedis.setRole(Role.USER);

        when(redisService.getUser("testUser")).thenReturn(userRedis);
        when(passwordEncoder.matches(loginRequest.getPassword(), userRedis.getPassword())).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("401 UNAUTHORIZED \"Invalid username or password\"", exception.getMessage());
    }

    @Test
    void testLoginUserNotFound() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");

        when(redisService.getUser("testUser")).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("401 UNAUTHORIZED \"Invalid username or password\"", exception.getMessage());
    }

    @Test
    void testLoginAuthenticationFailed() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("testPassword");

        UserRedis userRedis = new UserRedis();
        userRedis.setUsername("testUser");
        userRedis.setPassword("encodedPassword");
        userRedis.setRole(Role.USER);

        when(redisService.getUser("testUser")).thenReturn(userRedis);
        when(passwordEncoder.matches(loginRequest.getPassword(), userRedis.getPassword())).thenReturn(true);

        // Use BadCredentialsException instead of AuthenticationException
        doThrow(new BadCredentialsException("Invalid username or password"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("401 UNAUTHORIZED \"Invalid username or password\"", exception.getMessage());
    }
}
