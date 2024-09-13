package com.jwt.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import com.jwt.auth.service.implement.JwtServiceImplement;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

class JwtServiceImplementTest {

    @InjectMocks
    private JwtServiceImplement jwtService;

    @Mock
    private UserDetails userDetails;

    private String secretKey = "413F4428472B4B6250655368566D5970337336763979244226452948404D6351";
    private String token;
    private String username = "testUser";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock username untuk userDetails
        when(userDetails.getUsername()).thenReturn(username);

        // Membuat token JWT untuk testing
        token = Jwts.builder().setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void testGenerateToken() {
        String generatedToken = jwtService.generateToken(userDetails);
        assertNotNull(generatedToken);
        assertTrue(generatedToken.length() > 0);
    }

    @Test
    void testGenerateRefreshToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("extraClaim", "value");

        String refreshToken = jwtService.generateRefreshToken(claims, userDetails);
        assertNotNull(refreshToken);
        assertTrue(refreshToken.length() > 0);
    }

//    @Test
//    void testExtractUsername() {
//        String extractedUsername = jwtService.extractUsername(token);
//        assertEquals(username, extractedUsername);
//    }
//
//    @Test
//    void testIsTokenValid() {
//        boolean isValid = jwtService.isTokenValid(token, userDetails);
//        assertTrue(isValid);
//    }
//
//    @Test
//    void testIsTokenExpired() {
//        // Simulating an expired token by setting an expiration date in the past
//        String expiredToken = Jwts.builder().setSubject(username)
//                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24))
//                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60))
//                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
//                .compact();
//
//        boolean isExpired = jwtService.isTokenValid(expiredToken, userDetails);
//        assertFalse(isExpired);
//    }
//
//    @Test
//    void testExtractClaims() {
//        Claims claims = jwtService.extractAllClaims(token);
//        assertNotNull(claims);
//        assertEquals(username, claims.getSubject());
//    }
}
