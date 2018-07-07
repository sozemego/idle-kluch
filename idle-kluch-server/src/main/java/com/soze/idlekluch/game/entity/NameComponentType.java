package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.NameComponent;

public class NameComponentType extends ComponentType {

  @Override
  public Class returnedClass() {
    return NameComponent.class;
  }
}
