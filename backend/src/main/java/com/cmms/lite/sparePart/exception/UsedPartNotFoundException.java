package com.cmms.lite.sparePart.exception;

import com.cmms.lite.exception.ResourceNotFoundException;

public class UsedPartNotFoundException extends ResourceNotFoundException {
    public UsedPartNotFoundException(String message) {
        super(message);
    }
}