package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.OwnershipComponent;

public class OwnershipComponentType extends ComponentType {

  @Override
  public Class returnedClass() {
    return OwnershipComponent.class;
  }
}
