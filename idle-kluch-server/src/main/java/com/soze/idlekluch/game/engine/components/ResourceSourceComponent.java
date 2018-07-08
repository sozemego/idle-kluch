package com.soze.idlekluch.game.engine.components;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import java.util.Objects;

public class ResourceSourceComponent extends BaseComponent {

  private EntityUUID resourceId;
  private float bonus;

  public ResourceSourceComponent() {
    super(ComponentType.RESOURCE_SOURCE);
  }

  public ResourceSourceComponent(final EntityUUID resourceId, final float bonus) {
    this();
    this.resourceId = Objects.requireNonNull(resourceId);
    this.bonus = bonus;
  }

  public EntityUUID getResourceId() {
    return resourceId;
  }

  @JsonGetter("resourceId")
  public String getResourceIdString() {
    return resourceId.toString();
  }

  public void setResourceId(final EntityUUID resourceId) {
    this.resourceId = resourceId;
  }

  public float getBonus() {
    return bonus;
  }

  public void setBonus(final float bonus) {
    this.bonus = bonus;
  }

  @Override
  public ResourceSourceComponent copy() {
    return new ResourceSourceComponent(getResourceId(), getBonus());
  }
}
