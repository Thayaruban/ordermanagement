package com.zerobeta.assignment.ordermanagement.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private String secretKey = "YzJkNzJmNjlhMWFmZTQ1M2NhMzUwZTA1ZjAyMjA2YzYwYjQxZjY4ODNjMWUyNTcwYzg0MTg";
    private long jwtExpirationTime = 2000;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        jwtService = new JwtService();

        // Use reflection to access the private secretKey field and set the value
        Field secretKeyField = JwtService.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKeyField.set(jwtService, secretKey);

        // Use reflection to set the expiration time
        Field expirationField = JwtService.class.getDeclaredField("jwtExpirationTime");
        expirationField.setAccessible(true);
        expirationField.set(jwtService, jwtExpirationTime);
    }

    @Test
    void testGenerateToken() {
        String email = "user@example.com";
        String token = jwtService.generateToken(email);

        assertNotNull(token);
        assertFalse(token.isEmpty(), "Generated token should not be empty");
    }

    @Test
    void testExtractEmailFromToken() {
        String email = "user@example.com";
        String token = jwtService.generateToken(email);

        String extractedEmail = jwtService.extractEmail(token);

        assertEquals(email, extractedEmail, "Extracted email should match the original email");
    }

    @Test
    void testIsTokenExpired_False() {
        String email = "user@example.com";
        String token = jwtService.generateToken(email);

        boolean isExpired = jwtService.isTokenExpired(token);

        assertFalse(isExpired, "Token should not be expired");
    }

    @Test
    void testIsTokenExpired_True() {
        // Create a token that has already expired
        Map<String, Object> claims = new HashMap<>();
        String expiredToken = Jwts.builder()
                .setClaims(claims)
                .setSubject("test@example.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000)) // 2 seconds before current time
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // already expired
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        boolean isExpired = jwtService.isTokenExpired(expiredToken);

        assertTrue(isExpired, "Token should be expired");
    }

    @Test
    void testValidateToken_Valid() {
        String email = "user@example.com";
        String token = jwtService.generateToken(email);

        boolean isValid = jwtService.validateToken(token, email);

        assertTrue(isValid, "Token should be valid for the corresponding email");
    }

    @Test
    void testValidateToken_Invalid() {
        String email = "user@example.com";
        String token = jwtService.generateToken(email);

        boolean isValid = jwtService.validateToken(token, "invalid@example.com");

        assertFalse(isValid, "Token should not be valid for a different email");
    }

    @Test
    void testExtractExpiration() {
        String email = "test@example.com";
        String token = jwtService.generateToken(email);

        Date expiration = jwtService.extractExpiration(token);

        assertNotNull(expiration, "Expiration date should not be null");
        assertTrue(expiration.after(new Date()), "Expiration date should be in the future");
    }
}
