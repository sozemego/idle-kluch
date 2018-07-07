package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Component responsible for keeping an identifier of the owner
 * of the entity. Right now, they are users, so it contains both
 * the username and their id.
 */
@Entity
@Table(name = "ownership_components")
public class OwnershipComponent extends BaseComponent {

  @AttributeOverride(name = "id", column = @Column(name = "owner_id"))
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
