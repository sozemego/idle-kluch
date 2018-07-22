package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.ResourceSellerComponent;

public class ResourceSellerComponentUserType extends ComponentUserType {

  @Override
  public Class returnedClass() {
    return ResourceSellerComponent.class;
  }
}
