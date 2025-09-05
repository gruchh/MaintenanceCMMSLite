package com.cmms.lite.security.controller;

import com.cmms.lite.security.dto.*;
import com.cmms.lite.security.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@Slf4j
@RestController()
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class SecurityController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<JwtAuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Processing registration request for user: {}", request.getUsername());
        try {
            JwtAuthResponse response = userService.register(request);
            log.debug("Successfully registered and generated tokens for user: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Registration failed for user: {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(JwtAuthResponse.builder()
                            .message("Registration failed: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody JwtAuthRequest request) {
        log.info("Processing login request for user: {}", request.getUsername());
        try {
            JwtAuthResponse response = userService.verify(request);
            log.debug("Successfully verified user and generated tokens for: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(JwtAuthResponse.builder()
                            .message("Authentication failed: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtAuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Processing refresh token request");
        try {
            JwtAuthResponse response = userService.refreshToken(request);
            log.debug("Successfully refreshed token");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Invalid refresh token", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(JwtAuthResponse.builder()
                            .message("Invalid refresh token: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/getCurrentUser")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        log.info("Processing getCurrentUser request");
        try {
            UserProfileResponse response = userService.getCurrentUserInfo();
            response.setStatus("success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error while fetching user data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(UserProfileResponse.builder()
                            .status("error")
                            .message("Internal server error: " + e.getMessage())
                            .build());
        }
    }
}