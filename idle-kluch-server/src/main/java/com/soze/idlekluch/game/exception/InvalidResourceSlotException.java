package com.soze.idlekluch.game.exception;

import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceSourceSlot;

import java.util.UUID;

/**
 * Thrown when there is an issue with {@link ResourceSourceSlot}.
 */
public class InvalidResourceSlotException extends GameException {

  public InvalidResourceSlotException(final UUID messageId, final String message) {
    super(messageId, message);
  }

}
