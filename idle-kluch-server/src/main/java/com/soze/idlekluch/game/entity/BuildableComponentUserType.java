package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.BuildableComponent;

public class BuildableComponentUserType extends ComponentUserType {

  @Override
  public Class returnedClass() {
    return BuildableComponent.class;
  }
}
