package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.*;
import java.util.Objects;

/**
 * Specifies that this component is buildable.
 * Later this will be extended for sure, this component is now a marker.
 */
@Entity
@Table(name = "buildable_components")
public class BuildableComponent extends BaseComponent {

  @EmbeddedId
  @AttributeOverride(name = "id", column = @Column(name = "entity_id"))
  private EntityUUID entityId;

  public BuildableComponent() {
    super(ComponentType.BUILDABLE);
  }

  public BuildableComponent(final EntityUUID entityId) {
    this();
    this.entityId = Objects.requireNonNull(entityId);
  }

  public EntityUUID getEntityId() {
    return entityId;
  }

  public void setEntityId(final EntityUUID entityId) {
    this.entityId = Objects.requireNonNull(entityId);
  }

  @Override
  public BuildableComponent copy() {
    return new BuildableComponent(getEntityId());
  }
}
