package com.cmms.lite.sparePart.exception;

import com.cmms.lite.exception.ResourceNotFoundException;

public class SparePartNotFoundException extends ResourceNotFoundException {
    public SparePartNotFoundException(String message) {
        super(message);
    }
}