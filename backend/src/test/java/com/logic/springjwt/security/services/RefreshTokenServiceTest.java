package com.logic.springjwt.security.services;

import com.logic.springjwt.ExceptionHandler.TokenRefreshException;
import com.logic.springjwt.models.SecurityModel.RefreshToken;
import com.logic.springjwt.models.SecurityModel.User;
import com.logic.springjwt.models.SecurityModel.UserRole;
import com.logic.springjwt.repository.Securityrepo.RefreshTokenRepository;
import com.logic.springjwt.repository.Securityrepo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User sampleUser;
    private final Long refreshExpirationMs = 604800000L; // 7 days

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", refreshExpirationMs);

        sampleUser = new User();
        sampleUser.setUserId("user-123");
        sampleUser.setUsername("johndoe");
        sampleUser.setEmail("johndoe@example.com");
        sampleUser.setRole(UserRole.ROLE_USER);
        sampleUser.setActive(true);
    }

    @Test
    void testCreateRefreshTokenSets7DayExpiration() {
        when(userRepository.findByUserId("user-123")).thenReturn(Optional.of(sampleUser));
        when(refreshTokenRepository.findByUser(sampleUser)).thenReturn(Optional.empty());
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Instant before = Instant.now();
        RefreshToken token = refreshTokenService.createRefreshToken("user-123");
        Instant after = Instant.now();

        assertNotNull(token);
        assertNotNull(token.getToken());
        assertEquals(sampleUser, token.getUser());

        Instant expectedMin = before.plusMillis(refreshExpirationMs);
        Instant expectedMax = after.plusMillis(refreshExpirationMs);

        assertTrue(!token.getExpiryDate().isBefore(expectedMin));
        assertTrue(!token.getExpiryDate().isAfter(expectedMax));

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void testVerifyExpirationValidToken() {
        RefreshToken token = new RefreshToken();
        token.setToken("valid-token-uuid");
        token.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));

        RefreshToken verified = refreshTokenService.verifyExpiration(token);
        assertEquals(token, verified);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    void testVerifyExpirationExpiredTokenThrowsException() {
        RefreshToken expiredToken = new RefreshToken();
        expiredToken.setToken("expired-token-uuid");
        expiredToken.setExpiryDate(Instant.now().minusSeconds(10));

        TokenRefreshException ex = assertThrows(TokenRefreshException.class, () -> {
            refreshTokenService.verifyExpiration(expiredToken);
        });

        assertTrue(ex.getMessage().contains("Refresh token was expired"));
        verify(refreshTokenRepository).delete(expiredToken);
    }

    @Test
    void testDeleteByUserId() {
        when(userRepository.findByUserId("user-123")).thenReturn(Optional.of(sampleUser));
        when(refreshTokenRepository.deleteByUser(sampleUser)).thenReturn(1);

        int deletedCount = refreshTokenService.deleteByUserId("user-123");
        assertEquals(1, deletedCount);
        verify(refreshTokenRepository).deleteByUser(sampleUser);
    }
}
