package com.cmms.lite.security.service;

import com.cmms.lite.security.config.JwtProperties;
import com.cmms.lite.security.entity.Role;
import com.cmms.lite.security.exception.AppJwtException;
import com.cmms.lite.security.exception.JwtTokenExpiredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtProperties jwtProperties;

    private JwtService jwtService;

    private final String TEST_SECRET = "ASecretTestKeyThatIsLongEnoughForHS256TestingPurposes";
    private final String TEST_USERNAME = "testUser";
    private final String TEST_EMAIL = "test@user.com";
    private final Set<Role> TEST_ROLES = Set.of(Role.SUBCONTRACTOR);

    private final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        when(jwtProperties.getSecretKey()).thenReturn(TEST_SECRET);
        jwtService = new JwtService(jwtProperties, FIXED_CLOCK);
    }

    private void setUpTokenGeneration() {
        when(jwtProperties.getIssuer()).thenReturn("test-issuer");
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername_fromGeneratedToken() {
        setUpTokenGeneration();
        when(jwtProperties.getDuration()).thenReturn(1000L * 60 * 15);
        String token = jwtService.generateAccessToken(TEST_USERNAME, TEST_EMAIL, TEST_ROLES);

        String extractedUsername = jwtService.extractUsername(token);

        assertThat(token).isNotNull().isNotEmpty();
        assertThat(extractedUsername).isEqualTo(TEST_USERNAME);
    }

    @Test
    void extractAllClaims_shouldThrowJwtTokenExpiredException_whenTokenIsExpired() {
        setUpTokenGeneration();
        when(jwtProperties.getDuration()).thenReturn(-1000L);
        String expiredToken = jwtService.generateAccessToken(TEST_USERNAME, TEST_EMAIL, TEST_ROLES);

        assertThatThrownBy(() -> jwtService.extractAllClaims(expiredToken))
                .isInstanceOf(JwtTokenExpiredException.class)
                .hasMessage("JWT token has expired");
    }

    @Test
    void extractAllClaims_shouldThrowAppJwtException_whenTokenIsMalformed() {
        String malformedToken = "this.is.not.a.valid.token";

        assertThatThrownBy(() -> jwtService.extractAllClaims(malformedToken))
                .isInstanceOf(AppJwtException.class)
                .hasMessage("Invalid JWT token");
    }

    @Test
    void validateToken_shouldThrowJwtTokenExpiredException_whenTokenIsExpired() {
        setUpTokenGeneration();
        when(jwtProperties.getDuration()).thenReturn(-1000L);
        String expiredToken = jwtService.generateAccessToken(TEST_USERNAME, TEST_EMAIL, TEST_ROLES);
        UserDetails userDetails = User.withUsername(TEST_USERNAME).password("").roles("SUBCONTRACTOR").build();

        assertThatThrownBy(() -> jwtService.validateToken(expiredToken, userDetails))
                .isInstanceOf(JwtTokenExpiredException.class);
    }

    @Test
    void validateToken_shouldReturnTrue_whenTokenIsValidAndMatchesUser() {
        setUpTokenGeneration();
        when(jwtProperties.getDuration()).thenReturn(1000L * 60 * 15);
        String validToken = jwtService.generateAccessToken(TEST_USERNAME, TEST_EMAIL, TEST_ROLES);
        UserDetails userDetails = User.withUsername(TEST_USERNAME).password("").roles("SUBCONTRACTOR").build();

        boolean isValid = jwtService.validateToken(validToken, userDetails);

        assertThat(isValid).isTrue();
    }
}