package com.logic.springjwt.security.jwt;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    private final String secret = "MotE24sAQKrFVBH2070BKHsGFw20h8kFGHhterH18566234HJ32dwe42Gv14MotE24R5QKvFVB8gotE24sAQKrFVBH2070BKHsGFw20h8kFGHhthrH18566234HJ32dwe42Gv14MotE24R5QKvFVB8";
    private final int expirationMs = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", expirationMs);
    }

    @Test
    void testGenerateAndValidateToken() {
        String token = jwtUtils.generateTokenFromUsername("testuser");
        assertNotNull(token);
        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals("testuser", jwtUtils.getUserNameFromJwtToken(token));
    }

    @Test
    void testTokenExpirationTimeMatches24Hours() {
        long before = System.currentTimeMillis();
        String token = jwtUtils.generateTokenFromUsername("testuser");
        long after = System.currentTimeMillis();

        Claims claims = jwtUtils.getClaimsFromJwtToken(token);
        Date expiration = claims.getExpiration();
        assertNotNull(expiration);

        long expSeconds = expiration.getTime() / 1000;
        long expectedSecondsMin = (before + expirationMs) / 1000;
        long expectedSecondsMax = (after + expirationMs) / 1000;
        assertTrue(expSeconds >= expectedSecondsMin && expSeconds <= expectedSecondsMax);
    }

    @Test
    void testGenerateTokenWithExtraClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_ADMIN");
        claims.put("email", "admin@example.com");

        String token = jwtUtils.generateTokenFromUsername("adminuser", claims);
        assertNotNull(token);
        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals("adminuser", jwtUtils.getUserNameFromJwtToken(token));

        Claims parsedClaims = jwtUtils.getClaimsFromJwtToken(token);
        assertEquals("ROLE_ADMIN", parsedClaims.get("role"));
        assertEquals("admin@example.com", parsedClaims.get("email"));
    }

    @Test
    void testValidateInvalidToken() {
        assertFalse(jwtUtils.validateJwtToken("invalid.token.structure"));
        assertFalse(jwtUtils.validateJwtToken(""));
        assertFalse(jwtUtils.validateJwtToken(null));
    }

    @Test
    void testValidateExpiredToken() {
        // Set expiration to negative value to create an expired token
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", -1000);
        String expiredToken = jwtUtils.generateTokenFromUsername("expireduser");

        assertFalse(jwtUtils.validateJwtToken(expiredToken));
    }
}
