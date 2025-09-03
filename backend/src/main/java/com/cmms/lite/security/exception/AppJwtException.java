package com.cmms.lite.security.exception;

public class AppJwtException extends AppAuthenticationException {
    public AppJwtException(String message) {
        super(message);
    }

    public AppJwtException(String message, Throwable cause) {
        super(message, cause);
    }
}