package com.cmms.lite.security.exception;

public class JwtException extends AppAuthenticationException {
    public JwtException(String message) {
        super(message);
    }

    public JwtException(String message, Throwable cause) {
        super(message, cause);
    }
}