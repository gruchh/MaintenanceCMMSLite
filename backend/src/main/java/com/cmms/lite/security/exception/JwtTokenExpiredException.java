package com.cmms.lite.security.exception;

import io.jsonwebtoken.JwtException;

public class JwtTokenExpiredException extends JwtException {
    public JwtTokenExpiredException(String message) {
        super(message);
    }
}