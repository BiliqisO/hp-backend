package com.biliqis.hafsahs_place.service;

import com.biliqis.hafsahs_place.dto.AuthResponse;
import com.biliqis.hafsahs_place.dto.RegisterRequest;
import com.biliqis.hafsahs_place.exception.BadRequestException;
import com.biliqis.hafsahs_place.model.PasswordResetToken;
import com.biliqis.hafsahs_place.model.RefreshToken;
import com.biliqis.hafsahs_place.model.Role;
import com.biliqis.hafsahs_place.model.User;
import com.biliqis.hafsahs_place.repository.PasswordResetTokenRepository;
import com.biliqis.hafsahs_place.repository.RoleRepository;
import com.biliqis.hafsahs_place.repository.UserRepository;
import com.biliqis.hafsahs_place.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtTokenProvider tokenProvider;
    @Mock PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock EmailService emailService;
    @Mock RefreshTokenService refreshTokenService;

    @InjectMocks AuthService authService;

    private RegisterRequest registerRequest;
    private Role customerRole;
    private User savedUser;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("new@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("Amira");
        registerRequest.setLastName("Hassan");

        customerRole = Role.builder().id(1L).name("ROLE_CUSTOMER").build();

        savedUser = User.builder()
                .id(1L).email("new@test.com")
                .firstName("Amira").lastName("Hassan")
                .isActive(true)
                .build();

        refreshToken = RefreshToken.builder()
                .id(1L).token("refresh-uuid")
                .user(savedUser)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
    }

    // ── register ──────────────────────────────────────────────────────────────

    @Test
    void register_success_returnsAuthResponse() {
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_CUSTOMER")).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.save(any())).thenReturn(savedUser);

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(tokenProvider.generateToken(auth)).thenReturn("access-token");
        when(refreshTokenService.create(savedUser)).thenReturn(refreshToken);

        AuthResponse response = authService.register(registerRequest);

        assertThat(response.getToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-uuid");
        assertThat(response.getEmail()).isEqualTo("new@test.com");
        verify(emailService).sendWelcome(savedUser);
    }

    @Test
    void register_duplicateEmail_throws() {
        when(userRepository.existsByEmail("new@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already registered");
    }

    // ── forgotPassword ────────────────────────────────────────────────────────

    @Test
    void forgotPassword_knownEmail_sendsResetEmail() {
        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.of(savedUser));

        authService.forgotPassword("new@test.com");

        verify(passwordResetTokenRepository).deleteByUserId(savedUser.getId());
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordReset(eq("new@test.com"), eq("Amira"), anyString());
    }

    @Test
    void forgotPassword_unknownEmail_doesNotRevealAccount() {
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        // Should not throw — silent no-op for security
        assertThatCode(() -> authService.forgotPassword("ghost@test.com"))
                .doesNotThrowAnyException();

        verify(emailService, never()).sendPasswordReset(any(), any(), any());
    }

    // ── resetPassword ─────────────────────────────────────────────────────────

    @Test
    void resetPassword_validToken_changesPassword() {
        PasswordResetToken token = PasswordResetToken.builder()
                .token("valid-token")
                .user(savedUser)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

        when(passwordResetTokenRepository.findByTokenAndUsedFalse("valid-token"))
                .thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newpassword123")).thenReturn("hashed-new");
        when(userRepository.save(any())).thenReturn(savedUser);

        authService.resetPassword("valid-token", "newpassword123");

        assertThat(token.getUsed()).isTrue();
        verify(userRepository).save(argThat(u -> u.getPassword().equals("hashed-new")));
        verify(passwordResetTokenRepository).save(token);
    }

    @Test
    void resetPassword_expiredToken_throws() {
        PasswordResetToken token = PasswordResetToken.builder()
                .token("expired-token")
                .user(savedUser)
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .used(false)
                .build();

        when(passwordResetTokenRepository.findByTokenAndUsedFalse("expired-token"))
                .thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.resetPassword("expired-token", "newpassword123"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void resetPassword_invalidOrUsedToken_throws() {
        when(passwordResetTokenRepository.findByTokenAndUsedFalse("bad-token"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.resetPassword("bad-token", "newpassword123"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid or expired");
    }
}
