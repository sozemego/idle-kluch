package com.soze.idlekluch.game.engine.components;

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

  @Override
  public ResourceHarvesterComponent copy() {
    return new ResourceHarvesterComponent(
      getEntityId(),
      getResource(),
      getRadius(),
      getUnitsPerMinute()
    );
  }
}
