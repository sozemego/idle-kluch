package com.soze.idlekluch.core.exception;

import java.util.Objects;

public class EntityAlreadyExistsException extends RuntimeException {

  private final Class clazz;

  public EntityAlreadyExistsException(final String message, final Class clazz) {
    super(message);
    this.clazz = Objects.requireNonNull(clazz);
  }

  public Class getClazz() {
    return clazz;
  }

}
