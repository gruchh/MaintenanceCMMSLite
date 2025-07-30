package com.cmms.lite.security.controller;


import com.cmms.lite.security.dto.JwtAuthRequest;
import com.cmms.lite.security.dto.JwtAuthResponse;
import com.cmms.lite.security.dto.RegisterRequest;
import com.cmms.lite.security.dto.UserProfileResponse;
import com.cmms.lite.security.filter.JwtTokenValidator;
import com.cmms.lite.security.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@Slf4j
@RestController()
@RequestMapping("/auth")
public class SecurityController {

    private final UserService userService;
    private final JwtTokenValidator jwtTokenValidator;

    @PostMapping("/register")
    public ResponseEntity<JwtAuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Processing registration request for user: {}", request.getUsername());
        try {
            String jwtToken = userService.register(request);
            log.debug("Successfully generated JWT token for user: {}", request.getUsername());
            return ResponseEntity.ok(new JwtAuthResponse(jwtToken));
        } catch (Exception e) {
            log.error("Registration failed for user: {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new JwtAuthResponse(null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody JwtAuthRequest request) {
        log.info("Processing login request for user: {}", request.getUsername());
        try {
            String jwtToken = userService.verify(request);
            log.debug("Successfully verified user and generated JWT token for: {}", request.getUsername());
            return ResponseEntity.ok(new JwtAuthResponse(jwtToken));
        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new JwtAuthResponse(null));
        }
    }

    @GetMapping("/getCurrentUser")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        log.info("Przetwarzanie żądania getCurrentUser");
        try {
            UserProfileResponse response = userService.getCurrentUserInfo();
            response.setStatus("success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania danych użytkownika", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(UserProfileResponse.builder()
                            .status("error")
                            .message("Wewnętrzny błąd serwera: " + e.getMessage())
                            .build());
        }
    }
}