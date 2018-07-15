package com.soze.idlekluch.game.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import java.util.UUID;

/**
 * Slot numbers start at 1.
 */
public class AttachResourceSourceForm extends IncomingMessage {

  private final EntityUUID harvesterId;
  private final EntityUUID sourceId;
  private final int slot;

  @JsonCreator
  public AttachResourceSourceForm(@JsonProperty("messageId") final UUID messageId,
                                  @JsonProperty("harvesterId") final EntityUUID harvesterId,
                                  @JsonProperty("sourceId") final EntityUUID sourceId,
                                  @JsonProperty("slot") final int slot) {
    super(messageId, IncomingMessageType.ATTACH_RESOURCE_SOURCE);
    this.harvesterId = harvesterId;
    this.sourceId = sourceId;
    this.slot = slot;
  }

  public EntityUUID getHarvesterId() {
    return harvesterId;
  }

  public EntityUUID getSourceId() {
    return sourceId;
  }

  public int getSlot() {
    return slot;
  }
}
