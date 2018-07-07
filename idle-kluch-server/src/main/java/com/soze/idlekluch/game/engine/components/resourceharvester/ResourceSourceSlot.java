package com.soze.idlekluch.game.engine.components.resourceharvester;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;

public class ResourceSourceSlot {

  @JsonUnwrapped
  private final EntityUUID sourceId;
  private final int slotNumber;

  public ResourceSourceSlot(final EntityUUID sourceId, final int slotNumber) {
    this.sourceId = sourceId;
    this.slotNumber = slotNumber;
  }

  public EntityUUID getSourceId() {
    return sourceId;
  }

  public int getSlotNumber() {
    return slotNumber;
  }

  @Override
  public String toString() {
    return "ResourceSourceSlot{" +
             "sourceId=" + sourceId +
             ", slotNumber=" + slotNumber +
             '}';
  }
}
