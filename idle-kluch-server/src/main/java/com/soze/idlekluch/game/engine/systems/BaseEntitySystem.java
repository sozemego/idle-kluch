package com.soze.idlekluch.game.engine.systems;

import com.soze.klecs.engine.Engine;
import com.soze.klecs.system.EntitySystem;

import java.util.Objects;


public abstract class BaseEntitySystem implements EntitySystem {

  private final Engine engine;

  public BaseEntitySystem(final Engine engine) {
    this.engine = Objects.requireNonNull(engine);
  }

  @Override
  public boolean shouldUpdate(float delta) {
    return true;
  }

  @Override
  public Engine getEngine() {
    return engine;
  }

}
