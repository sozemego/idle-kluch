package com.soze.idlekluch.game.engine.components.resourceharvester;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;

public class ResourceSourceSlot {

  private EntityUUID sourceId;
  private int slotNumber;

  public ResourceSourceSlot() {
  }

  public ResourceSourceSlot(final EntityUUID sourceId, final int slotNumber) {
    this.sourceId = sourceId;
    this.slotNumber = slotNumber;
  }

  public void setSourceId(final EntityUUID sourceId) {
    this.sourceId = sourceId;
  }

  public void setSlotNumber(final int slotNumber) {
    this.slotNumber = slotNumber;
  }

  public EntityUUID getSourceId() {
    return sourceId;
  }

  @JsonGetter("sourceId")
  public String getSourceIdString() {
    return sourceId == null ? null : sourceId.toString();
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
