package com.cmms.lite.employee.exception;

import com.cmms.lite.exception.ResourceNotFoundException;

public class EmployeeRoleNotFoundException extends ResourceNotFoundException {
    public EmployeeRoleNotFoundException(String message) {
        super(message);
    }
}