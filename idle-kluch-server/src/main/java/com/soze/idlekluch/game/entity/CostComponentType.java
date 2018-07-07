package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.CostComponent;

public class CostComponentType extends ComponentType {

  @Override
  public Class returnedClass() {
    return CostComponent.class;
  }
}
