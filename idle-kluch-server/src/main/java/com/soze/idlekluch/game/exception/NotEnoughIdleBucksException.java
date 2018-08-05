package com.soze.idlekluch.game.exception;

import java.util.UUID;

public class NotEnoughIdleBucksException extends GameException {

  public NotEnoughIdleBucksException(final UUID messageId, final String message) {
    super(messageId, message);
  }

  public NotEnoughIdleBucksException(final UUID messageId) {
    super(messageId);
  }
}
