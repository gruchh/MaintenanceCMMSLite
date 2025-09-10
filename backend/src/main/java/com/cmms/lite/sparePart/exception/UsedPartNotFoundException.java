package com.cmms.lite.sparePart;

import com.cmms.lite.exception.ResourceNotFoundException;

public class UsedPartNotFoundException extends ResourceNotFoundException {
    public UsedPartNotFoundException(String message) {
        super(message);
    }
}