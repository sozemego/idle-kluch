package com.soze.idlekluch.kingdom.exception;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceSourceSlot;
import com.soze.idlekluch.game.exception.GameException;

import java.util.Objects;
import java.util.UUID;

/**
 * Thrown when there is an issue with {@link ResourceSourceSlot}.
 */
public class InvalidResourceSlotException extends GameException {

  private final EntityUUID entityId;
  private final int slot;

  public InvalidResourceSlotException(final UUID messageId, final String message, final EntityUUID entityId, final int slot) {
    super(messageId, message);
    this.entityId = Objects.requireNonNull(entityId);
    this.slot = slot;
  }

  public EntityUUID getEntityId() {
    return entityId;
  }

  public int getSlot() {
    return slot;
  }
}
