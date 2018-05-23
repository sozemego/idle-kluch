package com.soze.idlekluch.user.exception;

import java.util.Objects;

public class UserRegistrationException extends RuntimeException {

  private final String field;

  public UserRegistrationException(String field, String message) {
    super(message);
    this.field = Objects.requireNonNull(field);
  }

  public String getField() {
    return field;
  }


}
