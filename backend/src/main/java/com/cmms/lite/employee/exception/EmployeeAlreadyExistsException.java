package com.cmms.lite.employee.exception;

import com.cmms.lite.exception.ResourceNotFoundException;

public class EmployeeAlreadyExistsException extends ResourceNotFoundException {
    public EmployeeAlreadyExistsException(String message) {
        super(message);
    }
}