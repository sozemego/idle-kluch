package com.soze.idlekluch.game.engine.components;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import java.util.Objects;

/**
 * Component responsible for keeping an identifier of the owner
 * of the entity.
 */
public class OwnershipComponent extends BaseComponent {

  private EntityUUID ownerId;

  public OwnershipComponent() {
    super(ComponentType.OWNERSHIP);
  }

  public OwnershipComponent(final EntityUUID ownerId) {
    this();
    this.ownerId = Objects.requireNonNull(ownerId);
  }

  public EntityUUID getOwnerId() {
    return ownerId;
  }

  @JsonGetter("ownerId")
  public String getOwnerIdString() {
    return ownerId.toString();
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
