package com.cmms.lite.security.exception;

public class AppAuthenticationException extends RuntimeException {
  public AppAuthenticationException(String message) {
    super(message);
  }

  public AppAuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }
}