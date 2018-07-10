package com.soze.idlekluch.kingdom.exception;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.components.BaseComponent;
import com.soze.idlekluch.game.exception.GameException;
import com.soze.klecs.entity.Entity;

import java.util.Objects;
import java.util.UUID;

/**
 * Thrown when an {@link Entity} does not have a certain component,
 * but it is required to perform the action.
 */
public class EntityDoesNotHaveComponentException extends GameException {

  private final EntityUUID entityId;
  private final Class<BaseComponent> componentClass;

  public EntityDoesNotHaveComponentException(final UUID messageId, final EntityUUID entityId, final Class<BaseComponent> componentClass) {
    super(messageId);
    this.entityId = Objects.requireNonNull(entityId);
    this.componentClass = Objects.requireNonNull(componentClass);
  }

  public EntityUUID getEntityId() {
    return entityId;
  }

  public Class<BaseComponent> getComponentClass() {
    return componentClass;
  }

  @Override
  public String getMessage() {
    return "EntityId: " + entityId.toString() + " does not have a component: " + componentClass;
  }
}
