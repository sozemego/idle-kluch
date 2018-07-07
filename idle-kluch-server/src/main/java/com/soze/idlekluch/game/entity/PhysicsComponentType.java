package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.PhysicsComponent;

public class PhysicsComponentType extends ComponentType {

  @Override
  public Class returnedClass() {
    return PhysicsComponent.class;
  }
}
