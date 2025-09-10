package com.cmms.lite.employee.exception;

import com.cmms.lite.exception.ResourceNotFoundException;

public class EmployeeNotFoundException extends ResourceNotFoundException {
    public EmployeeNotFoundException(String message) {
        super(message);
    }
}