package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.ResourceSourceComponent;

public class ResourceSourceComponentUserType extends ComponentUserType {

  @Override
  public Class returnedClass() {
    return ResourceSourceComponent.class;
  }
}
