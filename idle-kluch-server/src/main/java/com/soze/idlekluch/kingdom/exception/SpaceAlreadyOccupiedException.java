package com.soze.idlekluch.kingdom.exception;

import com.soze.idlekluch.game.exception.GameException;

import java.util.UUID;

public class SpaceAlreadyOccupiedException extends GameException {

  public SpaceAlreadyOccupiedException(final UUID messageId, final String message) {
    super(messageId, message);
  }

}
