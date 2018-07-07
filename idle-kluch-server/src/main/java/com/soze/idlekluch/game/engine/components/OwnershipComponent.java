package com.soze.idlekluch.game.engine.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import java.util.Map;
import java.util.Objects;

/**
 * Component responsible for keeping an identifier of the owner
 * of the entity. Right now, they are users, so it contains both
 * the username and their id.
 */
public class OwnershipComponent extends BaseComponent {

  @JsonUnwrapped
  private EntityUUID ownerId;

  public OwnershipComponent() {
    super(ComponentType.OWNERSHIP);
  }

  public OwnershipComponent(final EntityUUID ownerId) {
    this();
    this.ownerId = Objects.requireNonNull(ownerId);
  }

  @JsonCreator
  public static OwnershipComponent factory(final Map<String, Object> properties) {
    return new OwnershipComponent(
      EntityUUID.fromString((String) properties.get("ownerId"))
    );
  }

  public EntityUUID getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(final EntityUUID ownerId) {
    this.ownerId = ownerId;
  }

  @Override
  public OwnershipComponent copy() {
    return new OwnershipComponent(getOwnerId());
  }

  @Override
  public String toString() {
    return "OwnershipComponent{" +
             "ownerId=" + ownerId +
             '}';
  }
}
