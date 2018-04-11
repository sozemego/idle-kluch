package com.soze.idlekluch.game.message;

import java.util.Objects;

public abstract class OutgoingMessage {

  private final OutgoingMessageType type;

  public OutgoingMessage(final OutgoingMessageType type) {
    this.type = Objects.requireNonNull(type);
  }

  public OutgoingMessageType getType() {
    return type;
  }

  public enum OutgoingMessageType {
    WORLD_CHUNK, CONSTRUCTED_BUILDING, ENTITY
  }

}
