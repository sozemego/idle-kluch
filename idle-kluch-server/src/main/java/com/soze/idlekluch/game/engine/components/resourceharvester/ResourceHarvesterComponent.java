package com.soze.idlekluch.game.engine.components.resourceharvester;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.components.BaseComponent;
import org.hibernate.annotations.Type;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResourceHarvesterComponent extends BaseComponent {

  private EntityUUID resourceId;
  private float radius;
  private int unitsPerMinute;
  private int sourceSlots;

  @Transient
  private final HarvestingProgress harvestingProgress = new HarvestingProgress();

  @Type(type = "jsonb")
  private List<ResourceSourceSlot> sources = new ArrayList<>();

  public ResourceHarvesterComponent() {
    super(ComponentType.RESOURCE_HARVESTER);
  }

  public ResourceHarvesterComponent(final EntityUUID resourceId,
                                    final float radius,
                                    final int unitsPerMinute,
                                    final int sourceSlots,
                                    final List<ResourceSourceSlot> sources) {
    this();
    this.resourceId = Objects.requireNonNull(resourceId);
    this.radius = radius;
    this.unitsPerMinute = unitsPerMinute;
    this.sourceSlots = sourceSlots;
    this.sources = Objects.requireNonNull(sources);
    fillSources(sources);
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

  public float getRadius() {
    return radius;
  }

  public void setRadius(final float radius) {
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
    this.fillSources(this.sources);
  }

  public void setSource(final EntityUUID entityId, final int index) {
    if(index > sourceSlots) {
      throw new IllegalArgumentException("This harvester only has " + sourceSlots + ", slot index " + index + " is not accessible.");
    }
    final List<ResourceSourceSlot> nextSources = new ArrayList<>(this.sources);
    fillSources(nextSources);
    nextSources.set(index, new ResourceSourceSlot(entityId, index));
    this.sources = new ArrayList<>(nextSources);
  }

  public void setSources(final List<ResourceSourceSlot> sources) {
    this.sources = sources;
  }

  private void fillSources(final List<ResourceSourceSlot> sources) {
    for(int i = 0; i < this.sourceSlots; i++) {
      if(sources.size() <= i) {
        sources.add(new ResourceSourceSlot(null, i));
      }
    }
  }

  @JsonProperty("sources")
  public List<ResourceSourceSlot> getSources() {
    return this.sources;
  }

  @Override
  public ResourceHarvesterComponent copy() {
    return new ResourceHarvesterComponent(
      getResourceId(),
      getRadius(),
      getUnitsPerMinute(),
      getSourceSlots(),
      getSources()
    );
  }

}
