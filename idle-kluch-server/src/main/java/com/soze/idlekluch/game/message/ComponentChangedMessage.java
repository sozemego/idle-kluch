package com.soze.idlekluch.game.message;

import com.soze.idlekluch.game.engine.components.BaseComponent.ComponentType;

import java.util.Objects;

public class ComponentChangedMessage extends OutgoingMessage {

  private final String entityId;
  private final ComponentType componentType;
  private final String field;
  private final Object data;

  public ComponentChangedMessage(final String entityId,
                                 final ComponentType componentType,
                                 final String field,
                                 final Object data) {
    super(OutgoingMessageType.COMPONENT_CHANGED);
    this.entityId = Objects.requireNonNull(entityId);
    this.componentType = Objects.requireNonNull(componentType);
    this.field = Objects.requireNonNull(field);
    this.data = Objects.requireNonNull(data);
  }

  public String getEntityId() {
    return entityId;
  }

  public ComponentType getComponentType() {
    return componentType;
  }

  public String getField() {
    return field;
  }

  public Object getData() {
    return data;
  }
}
