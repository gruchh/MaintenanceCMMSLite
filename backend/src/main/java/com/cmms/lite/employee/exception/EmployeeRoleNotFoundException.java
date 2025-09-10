package com.cmms.lite.exception;

public class EmployeeRoleNotFoundException extends ResourceNotFoundException {
    public EmployeeRoleNotFoundException(String message) {
        super(message);
    }
}