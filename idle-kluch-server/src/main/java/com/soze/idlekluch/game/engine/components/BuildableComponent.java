package com.soze.idlekluch.game.engine.components;

/**
 * Specifies that this component is buildable.
 * Later this will be extended for sure, this component is now a marker.
 */
public class BuildableComponent extends BaseComponent {

  public BuildableComponent() {
    super(ComponentType.BUILDABLE);
  }

  @Override
  public BuildableComponent copy() {
    return new BuildableComponent();
  }

  @Override
  public String toString() {
    return "BuildableComponent{}";
  }
}
