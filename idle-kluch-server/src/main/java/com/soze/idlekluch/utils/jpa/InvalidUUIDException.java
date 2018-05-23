package com.soze.idlekluch.utils.jpa;

import java.util.Objects;

public class InvalidUUIDException extends RuntimeException {

  private final String id;

  public InvalidUUIDException(final String id) {
    this.id = Objects.requireNonNull(id);
  }

  @Override
  public String getMessage() {
    return id + " is not a valid UUID";
  }

  public String getId() {
    return id;
  }
}
