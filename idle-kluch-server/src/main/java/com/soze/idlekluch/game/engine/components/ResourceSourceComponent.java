package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.kingdom.entity.Resource;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "resource_source_components")
public class ResourceSourceComponent extends BaseComponent {

  @OneToOne
  @JoinColumn(name = "resource_id")
  private Resource resource;

  @Column(name = "bonus")
  private float bonus;

  public ResourceSourceComponent() {
    super(ComponentType.RESOURCE_SOURCE);
  }

  public ResourceSourceComponent(final EntityUUID entityId, final Resource resource, final float bonus) {
    this();
    setEntityId(entityId);
    this.resource = Objects.requireNonNull(resource);
    this.bonus = bonus;
  }

  public Resource getResource() {
    return resource;
  }

  public void setResource(final Resource resource) {
    this.resource = resource;
  }

  public float getBonus() {
    return bonus;
  }

  public void setBonus(final float bonus) {
    this.bonus = bonus;
  }

  @Override
  public ResourceSourceComponent copy() {
    return new ResourceSourceComponent(getEntityId(), getResource(), getBonus());
  }
}
