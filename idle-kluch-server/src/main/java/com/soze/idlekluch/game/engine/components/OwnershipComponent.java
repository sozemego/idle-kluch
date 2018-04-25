package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Component responsible for keeping an identifier of the owner
 * of the entity. Right now, they are users, so it contains both
 * the username and their id.
 */
@Embeddable
public class OwnershipComponent extends BaseComponent {

  @AttributeOverride(name = "id", column = @Column(name = "owner_id"))
  private EntityUUID ownerId;

  public OwnershipComponent() {
    super(ComponentType.OWNERSHIP);
  }

  public EntityUUID getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(final EntityUUID ownerId) {
    this.ownerId = ownerId;
  }
}