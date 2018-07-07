package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.ResourceStorageComponent;

public class ResourceStorageComponentType extends ComponentType {

  @Override
  public Class returnedClass() {
    return ResourceStorageComponent.class;
  }

}
