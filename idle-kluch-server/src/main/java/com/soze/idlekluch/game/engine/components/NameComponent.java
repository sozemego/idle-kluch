package com.soze.idlekluch.game.engine.components;


import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Display name of the entity.
 */
@Entity
@Table(name = "name_components")
public class NameComponent extends BaseComponent {

  @Column(name = "name")
  private String name;

  public NameComponent() {
    super(ComponentType.NAME);
  }

  public NameComponent(final EntityUUID entityId, final String name) {
    this();
    setEntityId(entityId);
    this.name = Objects.requireNonNull(name);
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
