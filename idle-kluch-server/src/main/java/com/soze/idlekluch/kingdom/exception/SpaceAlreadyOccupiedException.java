package com.soze.idlekluch.kingdom.exception;

import java.util.Objects;
import java.util.UUID;

public class SpaceAlreadyOccupiedException extends RuntimeException {

  private final UUID messageId;

  public SpaceAlreadyOccupiedException(final UUID messageId) {
    this.messageId = Objects.requireNonNull(messageId);
  }

  public UUID getMessageId() {
    return messageId;
  }
}
