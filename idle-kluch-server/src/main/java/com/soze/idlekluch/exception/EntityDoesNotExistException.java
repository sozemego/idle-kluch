package com.soze.idlekluch.exception;

import java.util.Objects;

public class EntityDoesNotExistException extends RuntimeException {

  private final Class clazz;

  public EntityDoesNotExistException(final String message, final Class clazz) {
    super(message);
    this.clazz = Objects.requireNonNull(clazz);
  }

  public Class getClazz() {
    return clazz;
  }

}
