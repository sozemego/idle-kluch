package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.NameComponent;

public class NameComponentUserType extends ComponentUserType {

  @Override
  public Class returnedClass() {
    return NameComponent.class;
  }
}
