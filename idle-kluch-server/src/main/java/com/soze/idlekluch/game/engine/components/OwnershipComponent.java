package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.*;

/**
 * Component responsible for keeping an identifier of the owner
 * of the entity. Right now, they are users, so it contains both
 * the username and their id.
 */
@Entity
@Table(name = "ownership_components")
public class OwnershipComponent extends BaseComponent {

  @EmbeddedId
  @AttributeOverride(name = "id", column = @Column(name = "entity_id"))
  private EntityUUID entityId;

  @AttributeOverride(name = "id", column = @Column(name = "owner_id"))
  private EntityUUID ownerId;

  public OwnershipComponent() {
    super(ComponentType.OWNERSHIP);
  }

  public OwnershipComponent(final EntityUUID entityId, final EntityUUID ownerId) {
    this();
    this.entityId = entityId;
    this.ownerId = ownerId;
  }

  public EntityUUID getEntityId() {
    return entityId;
  }

  public void setEntityId(final EntityUUID entityId) {
    this.entityId = entityId;
  }

  public EntityUUID getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(final EntityUUID ownerId) {
    this.ownerId = ownerId;
  }

  @Override
  public OwnershipComponent copy() {
    return new OwnershipComponent(getEntityId(), getOwnerId());
  }
}
