package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.SellerComponent;

public class SellerComponentUserType extends ComponentUserType {

  @Override
  public Class returnedClass() {
    return SellerComponent.class;
  }
}
