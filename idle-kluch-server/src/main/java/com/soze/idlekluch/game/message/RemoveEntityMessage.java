package com.soze.idlekluch.game.message;

import java.util.Objects;

public class RemoveEntityMessage extends OutgoingMessage {

  private final String entityId;

  public RemoveEntityMessage(final String entityId) {
    super(OutgoingMessageType.REMOVE_ENTITY);
    this.entityId = Objects.requireNonNull(entityId);
  }

  public String getEntityId() {
    return entityId;
  }
}
