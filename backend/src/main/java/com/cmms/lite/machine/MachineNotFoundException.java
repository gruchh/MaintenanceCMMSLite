package com.cmms.lite.exception;

public class MachineNotFoundException extends ResourceNotFoundException {
  public MachineNotFoundException(String message) {
    super(message);
  }
}