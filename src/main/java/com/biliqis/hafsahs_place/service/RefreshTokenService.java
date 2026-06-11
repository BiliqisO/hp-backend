package com.biliqis.hafsahs_place.service;

import com.biliqis.hafsahs_place.exception.BadRequestException;
import com.biliqis.hafsahs_place.model.RefreshToken;
import com.biliqis.hafsahs_place.model.User;
import com.biliqis.hafsahs_place.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${app.jwt.refresh-expiration-days:7}")
    private int refreshExpirationDays;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken create(User user) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusDays(refreshExpirationDays))
                .build();
        return refreshTokenRepository.save(token);
    }

    /**
     * Validates the token and rotates it: revokes the old one and issues a fresh one.
     * Returns the new RefreshToken. Throws BadRequestException if invalid/expired.
     */
    @Transactional
    public RefreshToken rotate(String rawToken) {
        RefreshToken existing = refreshTokenRepository.findByTokenAndRevokedFalse(rawToken)
                .orElseThrow(() -> new BadRequestException("Invalid or expired refresh token"));

        if (LocalDateTime.now().isAfter(existing.getExpiresAt())) {
            existing.setRevoked(true);
            refreshTokenRepository.save(existing);
            throw new BadRequestException("Refresh token has expired, please log in again");
        }

        existing.setRevoked(true);
        refreshTokenRepository.save(existing);

        return create(existing.getUser());
    }

    /**
     * Revokes a specific refresh token (logout for one device).
     */
    @Transactional
    public void revoke(String rawToken) {
        refreshTokenRepository.findByTokenAndRevokedFalse(rawToken).ifPresent(t -> {
            t.setRevoked(true);
            refreshTokenRepository.save(t);
        });
    }

    /**
     * Revokes all refresh tokens for a user (logout everywhere).
     */
    @Transactional
    public void revokeAll(Long userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    public User getUserFromToken(String rawToken) {
        return refreshTokenRepository.findByTokenAndRevokedFalse(rawToken)
                .orElseThrow(() -> new BadRequestException("Invalid or expired refresh token"))
                .getUser();
    }
}
