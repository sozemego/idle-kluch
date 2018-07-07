package com.soze.idlekluch.game.engine.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import java.util.Map;
import java.util.Objects;

public class ResourceSourceComponent extends BaseComponent {

  @JsonUnwrapped
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

  @JsonCreator
  public static ResourceSourceComponent factory(final Map<String, Object> properties) {
    return new ResourceSourceComponent(
      EntityUUID.fromString((String) properties.get("resourceId")),
      Double.valueOf((double) properties.get("bonus")).floatValue()
    );
  }

  public EntityUUID getResourceId() {
    return resourceId;
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
