package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "resource_source_components")
public class ResourceSourceComponent extends BaseComponent {

  @OneToOne
  @JoinColumn(name = "resource_id")
  private Resource resource;

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

  @Override
  public BaseComponent copy() {
    return null;
  }
}
