package com.cmms.lite.machine;

import com.cmms.lite.exception.ResourceNotFoundException;

public class MachineNotFoundException extends ResourceNotFoundException {
  public MachineNotFoundException(String message) {
    super(message);
  }
}