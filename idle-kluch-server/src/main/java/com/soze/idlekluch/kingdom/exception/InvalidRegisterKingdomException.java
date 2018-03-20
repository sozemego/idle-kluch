package com.soze.idlekluch.kingdom.exception;

import java.util.Objects;

public class InvalidRegisterKingdomException extends RuntimeException {

  private final String field;
  private final String message;

  public InvalidRegisterKingdomException(String field, String message) {
    this.field = Objects.requireNonNull(field);
    this.message = Objects.requireNonNull(message);
  }

  public String getField() {
    return field;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
