package com.soze.idlekluch.game.engine.components;


import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.*;
import java.util.Objects;

/**
 * Display name of the entity.
 */
@Entity
@Table(name = "name_components")
public class NameComponent extends BaseComponent {

  @EmbeddedId
  @AttributeOverride(name = "id", column = @Column(name = "entity_id"))
  private EntityUUID entityId;

  @Column(name = "name")
  private String name;

  public NameComponent() {
    super(ComponentType.NAME);
  }

  public NameComponent(final EntityUUID entityId, final String name) {
    this();
    this.entityId = Objects.requireNonNull(entityId);
    this.name = Objects.requireNonNull(name);
  }

  public EntityUUID getEntityId() {
    return entityId;
  }

  public void setEntityId(final EntityUUID entityId) {
    this.entityId = Objects.requireNonNull(entityId);
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = Objects.requireNonNull(name);
  }

  @Override
  public NameComponent copy() {
    return new NameComponent(getEntityId(), getName());
  }
}
