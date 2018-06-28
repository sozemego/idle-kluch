package com.soze.idlekluch.game.message;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import java.util.Objects;

/**
 * Message used to inform the client that an entity started harvesting.
 * This is done so that the server and client start harvesting at the same time
 * and they don't drift apart in their updates.
 */
public class StartHarvestingMessage extends OutgoingMessage {

  @JsonUnwrapped
  private final EntityUUID entityId;

  public StartHarvestingMessage(final EntityUUID entityId) {
    super(OutgoingMessageType.START_HARVESTING);
    this.entityId = Objects.requireNonNull(entityId);
  }

  public EntityUUID getEntityId() {
    return entityId;
  }
}
