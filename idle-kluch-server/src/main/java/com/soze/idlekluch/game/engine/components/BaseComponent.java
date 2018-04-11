package com.soze.idlekluch.game.engine.components;

import java.util.Objects;

public abstract class BaseComponent {

  private final ComponentType componentType;

  public BaseComponent(final ComponentType componentType) {
    this.componentType = Objects.requireNonNull(componentType);
  }

  public ComponentType getComponentType() {
    return componentType;
  }

  public enum ComponentType {
    PHYSICS, GRAPHICS
  }

}
