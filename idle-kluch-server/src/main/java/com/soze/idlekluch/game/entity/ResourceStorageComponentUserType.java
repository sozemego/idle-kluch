package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.ResourceStorageComponent;

public class ResourceStorageComponentUserType extends ComponentUserType {

  @Override
  public Class returnedClass() {
    return ResourceStorageComponent.class;
  }

}
