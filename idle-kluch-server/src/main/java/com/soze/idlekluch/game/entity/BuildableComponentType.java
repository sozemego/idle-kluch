package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.BuildableComponent;

public class BuildableComponentType extends ComponentType {

  @Override
  public Class returnedClass() {
    return BuildableComponent.class;
  }
}
