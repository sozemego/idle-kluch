package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "resource_source_components")
public class ResourceSourceComponent extends BaseComponent {

  @OneToOne
  @JoinColumn(name = "resource_id")
  private Resource resource;

  @Transient
  private Set<com.soze.klecs.entity.Entity> harvesters = new HashSet<>();

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

  public Set<com.soze.klecs.entity.Entity> getHarvesters() {
    return harvesters;
  }

  public void addHarvester(final com.soze.klecs.entity.Entity harvester) {
    this.harvesters.add(harvester);
  }

  public void removeHarvester(final com.soze.klecs.entity.Entity harvester) {
    this.harvesters.remove(harvester);
  }

  @Override
  public ResourceSourceComponent copy() {
    return new ResourceSourceComponent(getEntityId(), getResource());
  }
}
