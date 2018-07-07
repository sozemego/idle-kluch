package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceHarvesterComponent;

public class ResourceHarvesterComponentType extends ComponentType {

  @Override
  public Class returnedClass() {
    return ResourceHarvesterComponent.class;
  }
}
