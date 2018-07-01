package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "resource_source_components")
public class ResourceSourceComponent extends BaseComponent {

  @OneToOne
  @JoinColumn(name = "resource_id")
  private Resource resource;

  @Transient
  private int harvesters;

  public ResourceSourceComponent() {
    super(ComponentType.RESOURCE_SOURCE);
  }

  public ResourceSourceComponent(final EntityUUID entityId, final Resource resource) {
    this();
    setEntityId(entityId);
    this.resource = Objects.requireNonNull(resource);
  }

  public Resource getResource() {
    return resource;
  }

  public void setResource(final Resource resource) {
    this.resource = resource;
  }

  public int getHarvesters() {
    return harvesters;
  }

  public void addHarvester() {
    this.harvesters++;
  }

  public void removeHarvester() {
    this.harvesters--;
  }

  @Override
  public ResourceSourceComponent copy() {
    return new ResourceSourceComponent(getEntityId(), getResource());
  }
}
