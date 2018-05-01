package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.*;

/**
 * A component for objects on which you cannot travel or build.
 */
@Entity
@Table(name = "static_occupy_space_components")
public class StaticOccupySpaceComponent extends BaseComponent {

  @EmbeddedId
  @AttributeOverride(name = "id", column = @Column(name = "entity_id"))
  private EntityUUID entityId;

  public StaticOccupySpaceComponent() {
    super(ComponentType.STATIC_OCCUPY_SPACE);
  }

  public StaticOccupySpaceComponent(final EntityUUID entityId) {
    this();
    this.entityId = entityId;
  }

  public EntityUUID getEntityId() {
    return entityId;
  }

  public void setEntityId(final EntityUUID entityId) {
    this.entityId = entityId;
  }

  @Override
  public StaticOccupySpaceComponent copy() {
    return new StaticOccupySpaceComponent(getEntityId());
  }
}
