package com.cmms.lite.security.service;

import com.cmms.lite.security.dto.*;
import com.cmms.lite.security.entity.RefreshToken;
import com.cmms.lite.security.entity.Role;
import com.cmms.lite.security.entity.User;
import com.cmms.lite.security.exception.TokenRefreshException;
import com.cmms.lite.security.mapper.UserResponseMapper;
import com.cmms.lite.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserResponseMapper userResponseMapper;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public JwtAuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException("User with this username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("User with this email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .roles(Set.of(Role.SUBCONTRACTOR))
                .build();

        userRepository.save(user);

        return verify(new JwtAuthRequest(request.getUsername(), request.getPassword()));
    }

    @Transactional
    public JwtAuthResponse verify(JwtAuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        String accessToken = jwtService.generateAccessToken(
                user.getUsername(),
                user.getEmail(),
                user.getRoles()
        );

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        return JwtAuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Transactional
    public JwtAuthResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtService.generateAccessToken(user.getUsername(), user.getEmail(), user.getRoles());

                    refreshTokenService.deleteByToken(requestRefreshToken);
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getUsername());

                    return JwtAuthResponse.builder()
                            .accessToken(newAccessToken)
                            .refreshToken(newRefreshToken.getToken())
                            .build();
                })
                .orElseThrow(() -> new TokenRefreshException("Refresh token is not in database!"));
    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        } else {
            throw new NoSuchElementException("No authenticated user found");
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
    }

    public UserProfileResponse getCurrentUserInfo() {
        User user = getCurrentUser();
        return userResponseMapper.toUserProfileResponse(user);
    }
}