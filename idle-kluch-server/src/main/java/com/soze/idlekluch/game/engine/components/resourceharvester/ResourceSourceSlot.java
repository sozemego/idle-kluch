package com.soze.idlekluch.game.engine.components.resourceharvester;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ResourceSourceSlot {

  @AttributeOverride(name = "id", column = @Column(name = "source_id"))
  @JsonUnwrapped
  private EntityUUID sourceId;

  @Column(name = "slot_number")
  private int slotNumber;

  public ResourceSourceSlot() {

  }

  public ResourceSourceSlot(final EntityUUID sourceId, final int slotNumber) {
    this.sourceId = sourceId;
    this.slotNumber = slotNumber;
  }

  public EntityUUID getSourceId() {
    return sourceId;
  }

  public void setSourceId(final EntityUUID sourceId) {
    this.sourceId = sourceId;
  }

  public int getSlotNumber() {
    return slotNumber;
  }

  public void setSlotNumber(final int slotNumber) {
    this.slotNumber = slotNumber;
  }

  @Override
  public String toString() {
    return "ResourceSourceSlot{" +
             "sourceId=" + sourceId +
             ", slotNumber=" + slotNumber +
             '}';
  }
}
