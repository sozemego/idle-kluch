package com.soze.idlekluch.game.message;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import java.util.UUID;

public class AttachResourceSourceForm extends IncomingMessage {

  private final UUID messageId;
  private final EntityUUID harvesterId;
  private final EntityUUID sourceId;
  private final int slot;

  public AttachResourceSourceForm(final UUID messageId, final EntityUUID harvesterId, final EntityUUID sourceId, final int slot) {
    super(messageId, IncomingMessageType.ATTACH_RESOURCE_SOURCE);
    this.messageId = messageId;
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
