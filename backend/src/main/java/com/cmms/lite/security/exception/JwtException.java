package com.cmms.lite.security.exception;

import org.springframework.http.HttpStatus;

public class JwtException extends ValidationException {
    public JwtException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public JwtException(String message, Throwable cause) {
        super(message, HttpStatus.UNAUTHORIZED);
        this.initCause(cause);
    }
}