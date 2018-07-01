package com.soze.idlekluch.game.engine.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.soze.idlekluch.core.utils.MathUtils;
import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "resource_harvester_components")
public class ResourceHarvesterComponent extends BaseComponent {

  @OneToOne
  @JoinColumn(name = "resource_id")
  private Resource resource;

  @Column(name = "radius")
  private float radius;

  @Column(name = "units_per_minute")
  private int unitsPerMinute;

  @Column(name = "source_slots")
  private int sourceSlots;

  @Transient
  private final HarvestingProgress harvestingProgress = new HarvestingProgress();

  @ElementCollection
  @CollectionTable(
    name = "resource_harvester_slots",
                    joinColumns = @JoinColumn(name = "entity_id")
  )
  @Column(name = "resource_id")
  private List<EntityUUID> sources = new ArrayList<>();

  public ResourceHarvesterComponent() {
    super(ComponentType.RESOURCE_HARVESTER);
  }

  public ResourceHarvesterComponent(final EntityUUID entityId,
                                    final Resource resource,
                                    final float radius,
                                    final int unitsPerMinute,
                                    final int sourceSlots,
                                    final List<EntityUUID> sources) {
    this();
    setEntityId(entityId);
    this.resource = Objects.requireNonNull(resource);
    this.radius = radius;
    this.unitsPerMinute = unitsPerMinute;
    this.sourceSlots = sourceSlots;
    this.sources = sources;
    fillSources();
  }

  public Resource getResource() {
    return resource;
  }

  public void setResource(final Resource resource) {
    this.resource = resource;
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
    this.fillSources();
  }

  public List<EntityUUID> getSources() {
    return this.sources;
  }

  @JsonProperty("sources")
  public List<String> getSourcesJson() {
    return getSources()
             .stream()
             .map(Object::toString)
             .collect(Collectors.toList());
  }

  public void setSource(final EntityUUID entityId, final int index) {
    if(index > sourceSlots) {
      throw new IllegalArgumentException("This harvester only has " + sourceSlots + ", slot index " + index + " is not accessible.");
    }
    final List<EntityUUID> nextSources = new ArrayList<>(this.sources);
    nextSources.set(index, entityId);
    this.sources = nextSources;
  }

  public void setSources(final List<EntityUUID> sources) {
    this.sources = sources;
  }

  private void fillSources() {
    for(int i = 0; i < this.sourceSlots; i++) {
      if(this.sources.size() <= i) {
        this.sources.add(null);
      }
    }
  }

  @Override
  public ResourceHarvesterComponent copy() {
    return new ResourceHarvesterComponent(
      getEntityId(),
      getResource(),
      getRadius(),
      getUnitsPerMinute(),
      getSourceSlots(),
      getSources()
    );
  }

  public static class HarvestingProgress {

    private HarvestingState harvestingState;
    private float harvestingProgressPercent;

    public HarvestingProgress() {
      this.harvestingState = HarvestingState.WAITING;
      this.harvestingProgressPercent = 0f;
    }

    public HarvestingState getHarvestingState() {
      return harvestingState;
    }

    public void setHarvestingState(final HarvestingState harvestingState) {
      this.harvestingState = harvestingState;
    }

    public float getHarvestingProgressPercent() {
      return harvestingProgressPercent;
    }

    public void setHarvestingProgressPercent(final float harvestingProgressPercent) {
      this.harvestingProgressPercent = harvestingProgressPercent;
    }

    @JsonIgnore
    public boolean isFinished() {
      return MathUtils.equals(getHarvestingProgressPercent(), 1f, 0.05f);
    }

    public void start() {
      setHarvestingState(HarvestingState.HARVESTING);
      setHarvestingProgressPercent(0f);
    }

    public void stop() {
      setHarvestingState(HarvestingState.WAITING);
      setHarvestingProgressPercent(0f);
    }
  }

  public enum HarvestingState {
    HARVESTING, WAITING
  }


}
