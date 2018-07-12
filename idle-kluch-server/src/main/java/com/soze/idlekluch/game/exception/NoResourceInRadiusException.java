package com.soze.idlekluch.game.exception;

import java.util.UUID;

public class NoResourceInRadiusException extends GameException {

  public NoResourceInRadiusException(final UUID messageId, final String message) {
    super(messageId, message);
  }
}
