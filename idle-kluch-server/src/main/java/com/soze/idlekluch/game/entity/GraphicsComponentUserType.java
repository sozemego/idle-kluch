package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.GraphicsComponent;

public class GraphicsComponentUserType extends ComponentUserType {

  @Override
  public Class returnedClass() {
    return GraphicsComponent.class;
  }
}
