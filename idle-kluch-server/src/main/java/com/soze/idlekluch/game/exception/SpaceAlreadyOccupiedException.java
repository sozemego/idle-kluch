package com.soze.idlekluch.game.exception;

import java.util.UUID;

public class SpaceAlreadyOccupiedException extends GameException {

  public SpaceAlreadyOccupiedException(final UUID messageId, final String message) {
    super(messageId, message);
  }

}
