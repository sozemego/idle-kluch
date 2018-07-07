package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.GraphicsComponent;

public class GraphicsComponentType extends ComponentType {

  @Override
  public Class returnedClass() {
    return GraphicsComponent.class;
  }
}
