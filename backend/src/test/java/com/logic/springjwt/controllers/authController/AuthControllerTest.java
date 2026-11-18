package com.logic.springjwt.controllers.authController;

import com.logic.springjwt.DTO.SecurityDTO.LoginRequest;
import com.logic.springjwt.DTO.SecurityDTO.TokenRefreshRequest;
import com.logic.springjwt.DTO.SecurityDTO.TokenRefreshResponse;
import com.logic.springjwt.ExceptionHandler.TokenRefreshException;
import com.logic.springjwt.models.SecurityModel.RefreshToken;
import com.logic.springjwt.models.SecurityModel.User;
import com.logic.springjwt.models.SecurityModel.UserRole;
import com.logic.springjwt.response.JwtResponse;
import com.logic.springjwt.security.jwt.JwtUtils;
import com.logic.springjwt.security.services.AuthService;
import com.logic.springjwt.security.services.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    @Test
    void testLoginReturnsAccessAndRefreshToken() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        JwtResponse mockResponse = new JwtResponse(
                "mock-access-token-jwt",
                "mock-refresh-token-uuid",
                "user-id-123",
                "testuser",
                "testuser@example.com",
                List.of("ROLE_USER")
        );

        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(mockResponse);

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof JwtResponse);

        String setCookieHeader = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertNotNull(setCookieHeader);
        assertTrue(setCookieHeader.contains("accessToken=mock-access-token-jwt"));
        assertTrue(setCookieHeader.contains("Path=/"));
        assertTrue(setCookieHeader.contains("Max-Age=86400"));
        assertTrue(setCookieHeader.contains("SameSite=Lax"));

        JwtResponse body = (JwtResponse) response.getBody();
        assertEquals("mock-access-token-jwt", body.getAccessToken());
        assertEquals("mock-refresh-token-uuid", body.getRefreshToken());
        assertEquals("testuser", body.getUsername());
    }

    @Test
    void testRefreshTokenSuccess() {
        TokenRefreshRequest request = new TokenRefreshRequest("valid-refresh-token");

        User user = new User("user-1", "testuser", "test@example.com", "pass", UserRole.ROLE_USER, true);
        RefreshToken refreshToken = new RefreshToken(1L, user, "valid-refresh-token", Instant.now().plusSeconds(3600));

        when(refreshTokenService.findByToken("valid-refresh-token")).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
        when(jwtUtils.generateTokenFromUsername("testuser")).thenReturn("new-generated-access-token");

        ResponseEntity<?> response = authController.refreshtoken(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof TokenRefreshResponse);

        String setCookieHeader = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertNotNull(setCookieHeader);
        assertTrue(setCookieHeader.contains("accessToken=new-generated-access-token"));

        TokenRefreshResponse body = (TokenRefreshResponse) response.getBody();
        assertEquals("new-generated-access-token", body.getAccessToken());
        assertEquals("valid-refresh-token", body.getRefreshToken());
        assertEquals("Bearer", body.getTokenType());
    }

    @Test
    void testRefreshTokenNotFoundThrowsTokenRefreshException() {
        TokenRefreshRequest request = new TokenRefreshRequest("non-existent-refresh-token");

        when(refreshTokenService.findByToken("non-existent-refresh-token")).thenReturn(Optional.empty());

        assertThrows(TokenRefreshException.class, () -> {
            authController.refreshtoken(request);
        });
    }

    @Test
    void testLogoutExpiresAccessTokenCookie() {
        ResponseEntity<?> response = authController.logoutUser();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        String setCookieHeader = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertNotNull(setCookieHeader);
        assertTrue(setCookieHeader.contains("accessToken="));
        assertTrue(setCookieHeader.contains("Max-Age=0"));
        assertTrue(setCookieHeader.contains("Path=/"));
    }
}
