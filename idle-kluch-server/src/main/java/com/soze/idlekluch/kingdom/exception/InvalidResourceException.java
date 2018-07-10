package com.soze.idlekluch.kingdom.exception;

import com.soze.idlekluch.game.exception.GameException;
import com.soze.idlekluch.kingdom.entity.Resource;

import java.util.Objects;
import java.util.UUID;

/**
 * Thrown when there is something wrong with given {@link Resource}.
 */
public class InvalidResourceException extends GameException {

  private final Resource resource;

  public InvalidResourceException(final UUID messageId, final Resource resource, final String message) {
    super(messageId, message);
    this.resource = Objects.requireNonNull(resource);
  }

  public Resource getResource() {
    return resource;
  }
}
