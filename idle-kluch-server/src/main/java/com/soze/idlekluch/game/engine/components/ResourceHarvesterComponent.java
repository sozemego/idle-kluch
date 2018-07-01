package com.soze.idlekluch.game.engine.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.soze.idlekluch.core.utils.MathUtils;
import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import javax.persistence.*;
import java.util.Objects;

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

  @Transient
  private final HarvestingProgress harvestingProgress = new HarvestingProgress();

  @Transient
  private int sources;

  public ResourceHarvesterComponent() {
    super(ComponentType.RESOURCE_HARVESTER);
  }

  public ResourceHarvesterComponent(final EntityUUID entityId,
                                    final Resource resource,
                                    final float radius,
                                    final int unitsPerMinute) {
    this();
    setEntityId(entityId);
    this.resource = Objects.requireNonNull(resource);
    this.radius = radius;
    this.unitsPerMinute = unitsPerMinute;
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

  public long getSources() {
    return this.sources;
  }

  public void addSource() {
    this.sources++;
  }

  public void removeSource() {
    this.sources--;
  }

  @Override
  public ResourceHarvesterComponent copy() {
    return new ResourceHarvesterComponent(
      getEntityId(),
      getResource(),
      getRadius(),
      getUnitsPerMinute()
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
