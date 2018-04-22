package com.soze.idlekluch.game.message;

import com.soze.idlekluch.game.engine.components.BaseComponent;
import com.soze.idlekluch.utils.jpa.EntityUUID;

import java.util.List;
import java.util.Objects;

public class EntityMessage extends OutgoingMessage {

  private final EntityUUID id;

  private final List<BaseComponent> components;

  public EntityMessage(final EntityUUID id, final List<BaseComponent> components) {
    super(OutgoingMessageType.ENTITY);
    this.id = id;
    this.components = Objects.requireNonNull(components);
  }

  public EntityUUID getId() {
    return id;
  }

  public List<BaseComponent> getComponents() {
    return components;
  }

}
