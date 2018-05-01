package com.soze.idlekluch.game.engine.components;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.*;
import java.util.Objects;

@MappedSuperclass
public abstract class BaseComponent {

  @EmbeddedId
  @AttributeOverride(name = "id", column = @Column(name = "entity_id"))
  @JsonUnwrapped
  private EntityUUID entityId;

  @Transient
  private ComponentType componentType;

  public BaseComponent() {

  }

  public BaseComponent(final ComponentType componentType) {
    this.componentType = Objects.requireNonNull(componentType);
  }

  public ComponentType getComponentType() {
    return componentType;
  }

  public EntityUUID getEntityId() {
    return entityId;
  }

  public void setEntityId(final EntityUUID entityId) {
    this.entityId = Objects.requireNonNull(entityId);
  }

  /**
   * A method which copies (clones) the component.
   * This is to implement a Template pattern for entities, so that they can be loaded
   * from a file and then copied when they need to be added to the game.
   * This will stop us from having a parallel line of objects which describe how to convert
   * data from file to entity.
   */
  public abstract BaseComponent copy();

  public enum ComponentType {
    PHYSICS, GRAPHICS, OWNERSHIP, STATIC_OCCUPY_SPACE, NAME, BUILDABLE,
  }

}
