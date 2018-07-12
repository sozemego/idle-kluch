package com.soze.idlekluch.kingdom.exception;

import com.soze.idlekluch.game.exception.GameException;
import com.soze.idlekluch.kingdom.entity.Resource;

import java.util.Objects;
import java.util.UUID;

/**
 * Thrown when there is something wrong with a {@link Resource}.
 */
public class InvalidResourceException extends GameException {

  public InvalidResourceException(final UUID messageId, final String message) {
    super(messageId, message);
  }

}
