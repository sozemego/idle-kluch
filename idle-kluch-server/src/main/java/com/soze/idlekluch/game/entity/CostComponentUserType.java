package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.CostComponent;

public class CostComponentUserType extends ComponentUserType {

  @Override
  public Class returnedClass() {
    return CostComponent.class;
  }
}
