package com.soze.idlekluch.game.exception;

import java.util.UUID;

public class InvalidOwnerException extends GameException {

  public InvalidOwnerException(final UUID messageId, final String message) {
    super(messageId, message);
  }

}
