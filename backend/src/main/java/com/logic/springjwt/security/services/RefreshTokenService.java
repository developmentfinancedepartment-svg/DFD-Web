package com.logic.springjwt.security.services;

import com.logic.springjwt.ExceptionHandler.TokenRefreshException;
import com.logic.springjwt.models.SecurityModel.RefreshToken;
import com.logic.springjwt.models.SecurityModel.User;
import com.logic.springjwt.repository.Securityrepo.RefreshTokenRepository;
import com.logic.springjwt.repository.Securityrepo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${jwt.refreshExpirationMs:604800000}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(String userId) {
        User user = userRepository.findByUserId(userId)
                .or(() -> userRepository.findById(userId))
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Reuse / update existing token if already present for the user to maintain 1:1 relation
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElse(new RefreshToken());

        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .or(() -> userRepository.findById(userId))
                .map(refreshTokenRepository::deleteByUser)
                .orElse(0);
    }

    @Transactional
    public int deleteByUser(User user) {
        return refreshTokenRepository.deleteByUser(user);
    }
}
