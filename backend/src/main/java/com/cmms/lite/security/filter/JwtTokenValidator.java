package com.cmms.lite.security.filter;

import com.cmms.lite.security.exception.JwtException;
import com.cmms.lite.security.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtTokenValidator {

    private final JwtService jwtService;

    public String validateToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new JwtException("Brak lub nieprawidłowy nagłówek Authorization");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        if (username == null || !jwtService.isTokenValid(token, username)) {
            throw new JwtException("Nieprawidłowy lub wygasły token JWT");
        }

        return username;
    }
}