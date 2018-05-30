package com.soze.idlekluch.exception;

import java.util.Objects;

public class InvalidFormException extends RuntimeException {

  private final String propertyPath;
  private final String message;

  public InvalidFormException(final String propertyPath, final String message) {
    this.propertyPath = Objects.requireNonNull(propertyPath);
    this.message = Objects.requireNonNull(message);
  }

  public String getPropertyPath() {
    return propertyPath;
  }

  @Override
  public String getMessage() {
    return message;
  }

}
