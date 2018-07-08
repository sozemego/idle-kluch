package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.OwnershipComponent;

public class OwnershipComponentUserType extends ComponentUserType {

  @Override
  public Class returnedClass() {
    return OwnershipComponent.class;
  }
}
