package com.soze.idlekluch.game.message;

import com.soze.idlekluch.game.engine.components.BaseComponent;

import java.util.List;
import java.util.Objects;

public class EntityMessage extends OutgoingMessage {

  private final long id;

  private final List<BaseComponent> components;

  public EntityMessage(final long id, final List<BaseComponent> components) {
    super(OutgoingMessageType.ENTITY);
    this.id = id;
    this.components = Objects.requireNonNull(components);
  }

  public long getId() {
    return id;
  }

  public List<BaseComponent> getComponents() {
    return components;
  }

}
