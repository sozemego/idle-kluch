package com.soze.idlekluch.game.engine.systems;

import com.soze.klecs.engine.Engine;
import com.soze.klecs.entity.Entity;

import java.util.Set;

/**
 * A system responsible for physical aspects of an entity.
 * This includes: position, size.
 */
public class PhysicsSystem extends BaseEntitySystem {

  public PhysicsSystem(final Engine engine, final Set<Entity> changedEntities) {
    super(engine, changedEntities);
  }

  @Override
  public void update(float delta) {

  }

}
