package com.soze.idlekluch.game.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.soze.idlekluch.game.engine.components.BaseComponent;
import com.soze.idlekluch.utils.jpa.EntityUUID;

import java.util.List;
import java.util.Objects;

public class EntityMessage extends OutgoingMessage {

  private final List<BaseComponent> components;

  private final EntityUUID entityId;

  public EntityMessage(final EntityUUID entityId,
                       final List<BaseComponent> components) {
    super(OutgoingMessageType.ENTITY);
    this.entityId = Objects.requireNonNull(entityId);
    this.components = Objects.requireNonNull(components);
  }

  @JsonCreator
  public static EntityMessage create(@JsonProperty("id") final EntityUUID id,
                                     @JsonProperty("components") final List<BaseComponent> components) {
    return new EntityMessage(id, components);
  }

  @JsonGetter(value = "id")
  public String getId() {
    return entityId.toString();
  }

  public List<BaseComponent> getComponents() {
    return components;
  }

  @Override
  public String toString() {
    return "EntityMessage{" +
             "entityId=" + entityId +
             ", components=" + components +
             '}';
  }
}
