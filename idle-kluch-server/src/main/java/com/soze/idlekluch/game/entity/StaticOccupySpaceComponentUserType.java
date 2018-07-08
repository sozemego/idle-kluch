package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.StaticOccupySpaceComponent;

public class StaticOccupySpaceComponentUserType extends ComponentUserType {

  @Override
  public Class returnedClass() {
    return StaticOccupySpaceComponent.class;
  }
}
