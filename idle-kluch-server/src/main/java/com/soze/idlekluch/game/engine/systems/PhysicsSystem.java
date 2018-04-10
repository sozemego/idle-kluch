package com.soze.idlekluch.game.engine.systems;

import com.soze.klecs.engine.Engine;

/**
 * A system responsible for physical aspects of an entity.
 * This includes: position, size.
 */
public class PhysicsSystem extends BaseEntitySystem {

  public PhysicsSystem(final Engine engine) {
    super(engine);
  }

  @Override
  public void update(float delta) {

  }

}
