package com.soze.idlekluch.game.engine.components.resourceharvester;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.components.BaseComponent;
import org.hibernate.annotations.Type;

import javax.persistence.Transient;
import java.util.*;

public class ResourceHarvesterComponent extends BaseComponent {

  private EntityUUID resourceId;
  private int radius;
  private int unitsPerMinute;
  private int sourceSlots;

  @Transient
  private final HarvestingProgress harvestingProgress = new HarvestingProgress();

  @Type(type = "jsonb")
  private List<ResourceSourceSlot> slots = new ArrayList<>();

  public ResourceHarvesterComponent() {
    super(ComponentType.RESOURCE_HARVESTER);
  }

  public ResourceHarvesterComponent(final EntityUUID resourceId,
                                    final int radius,
                                    final int unitsPerMinute,
                                    final int sourceSlots,
                                    final List<ResourceSourceSlot> slots) {
    this();
    this.resourceId = Objects.requireNonNull(resourceId);
    this.radius = radius;
    this.unitsPerMinute = unitsPerMinute;
    this.sourceSlots = sourceSlots;
    this.slots = Objects.requireNonNull(slots);
    fillSlots(slots);
  }

  public EntityUUID getResourceId() {
    return resourceId;
  }

  @JsonGetter("resourceId")
  public String getResourceIdString() {
    return resourceId.toString();
  }

  public void setResourceId(final EntityUUID resourceId) {
    Objects.requireNonNull(resourceId.getId());
    this.resourceId = Objects.requireNonNull(resourceId);
  }

  public int getRadius() {
    return radius;
  }

  public void setRadius(final int radius) {
    this.radius = radius;
  }

  public int getUnitsPerMinute() {
    return unitsPerMinute;
  }

  public void setUnitsPerMinute(final int unitsPerMinute) {
    this.unitsPerMinute = unitsPerMinute;
  }

  public HarvestingProgress getHarvestingProgress() {
    return harvestingProgress;
  }

  public int getSourceSlots() {
    return sourceSlots;
  }

  public void setSourceSlots(final int sourceSlots) {
    this.sourceSlots = sourceSlots;
    this.fillSlots(this.slots);
  }

  public void setSlot(final EntityUUID entityId, final int index) {
    if(index > sourceSlots) {
      throw new IllegalArgumentException("This harvester only has " + sourceSlots + ", slot index " + index + " is not accessible.");
    }
    final List<ResourceSourceSlot> nextSlots = new ArrayList<>(this.slots);
    fillSlots(nextSlots);
    nextSlots.set(index, new ResourceSourceSlot(entityId, index));
    this.slots = new ArrayList<>(nextSlots);
  }

  public void setSlots(final List<ResourceSourceSlot> slots) {
    this.slots = slots;
  }

  private void fillSlots(final List<ResourceSourceSlot> slots) {
    for(int i = 0; i < this.sourceSlots; i++) {
      if(slots.size() <= i) {
        slots.add(new ResourceSourceSlot(null, i));
      }
    }
  }

  @JsonProperty("slots")
  public List<ResourceSourceSlot> getSlots() {
    Collections.sort(this.slots, Comparator.comparingInt(ResourceSourceSlot::getSlotNumber));
    return this.slots;
  }

  @Override
  public ResourceHarvesterComponent copy() {
    return new ResourceHarvesterComponent(
      getResourceId(),
      getRadius(),
      getUnitsPerMinute(),
      getSourceSlots(),
      getSlots()
    );
  }

}
