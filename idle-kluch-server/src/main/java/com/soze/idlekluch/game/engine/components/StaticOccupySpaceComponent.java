package com.soze.idlekluch.game.engine.components;

/**
 * A component for objects on which you cannot travel or build.
 */
public class StaticOccupySpaceComponent extends BaseComponent {

  public StaticOccupySpaceComponent() {
    super(ComponentType.STATIC_OCCUPY_SPACE);
  }

  @Override
  public StaticOccupySpaceComponent copy() {
    return new StaticOccupySpaceComponent();
  }

  @Override
  public String toString() {
    return "StaticOccupySpaceComponent{}";
  }
}
