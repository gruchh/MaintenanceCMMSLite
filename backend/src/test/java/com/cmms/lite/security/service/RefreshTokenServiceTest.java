package com.cmms.lite.security.service;

import com.cmms.lite.security.config.JwtProperties;
import com.cmms.lite.security.entity.RefreshToken;
import com.cmms.lite.security.entity.User;
import com.cmms.lite.security.exception.TokenRefreshException;
import com.cmms.lite.security.repository.RefreshTokenRepository;
import com.cmms.lite.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtProperties jwtProperties;

    private final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2025-01-01T12:00:00Z"), ZoneOffset.UTC);

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenService(refreshTokenRepository, userRepository, jwtProperties, FIXED_CLOCK);
    }

    @Test
    void shouldCreateAndSaveRefreshTokenSuccessfully() {
        String username = "testUser";
        User user = User.builder().username(username).build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtProperties.getRefreshDuration()).thenReturn(604800000L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken createdToken = refreshTokenService.createRefreshToken(username);

        assertThat(createdToken).isNotNull();
        assertThat(createdToken.getUser()).isEqualTo(user);
        assertThat(createdToken.getToken()).isNotNull().isNotEmpty();
        assertThat(createdToken.getExpiryDate()).isEqualTo("2025-01-08T12:00:00Z");
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void shouldThrowExceptionWhenVerifyingExpiredToken() {
        RefreshToken expiredToken = RefreshToken.builder()
                .token("expired-token")
                .expiryDate(Instant.parse("2025-01-01T11:00:00Z"))
                .build();

        assertThatThrownBy(() -> refreshTokenService.verifyExpiration(expiredToken))
                .isInstanceOf(TokenRefreshException.class)
                .hasMessage("Refresh token was expired. Please make a new signin request");

        verify(refreshTokenRepository, times(1)).delete(expiredToken);
    }

    @Test
    void shouldReturnTokenWhenVerifyingValidToken() {
        RefreshToken validToken = RefreshToken.builder()
                .token("valid-token")
                .expiryDate(Instant.parse("2025-01-01T13:00:00Z"))
                .build();

        RefreshToken result = refreshTokenService.verifyExpiration(validToken);

        assertThat(result).isEqualTo(validToken);
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }
}