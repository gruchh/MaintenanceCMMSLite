package com.cmms.lite.exception;

public class EmployeeNotFoundException extends ResourceNotFoundException {
    public EmployeeNotFoundException(String message) {
        super(message);
    }
}