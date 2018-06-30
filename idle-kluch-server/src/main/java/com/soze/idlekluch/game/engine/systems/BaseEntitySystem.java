package com.soze.idlekluch.game.engine.systems;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.klecs.engine.Engine;
import com.soze.klecs.entity.Entity;
import com.soze.klecs.system.EntitySystem;

import java.util.Map;
import java.util.Objects;
import java.util.Set;


public abstract class BaseEntitySystem implements EntitySystem {

  private final Engine engine;

  /**
   * When an entity changes, in a way that should be persisted,
   * it will be added here.
   */
  private final Map<EntityUUID, Entity> changedEntities;

  public BaseEntitySystem(final Engine engine, final Map<EntityUUID, Entity> changedEntities) {
    this.engine = Objects.requireNonNull(engine);
    this.changedEntities = Objects.requireNonNull(changedEntities);
  }

  @Override
  public boolean shouldUpdate(float delta) {
    return true;
  }

  @Override
  public Engine getEngine() {
    return engine;
  }

  public void addChangedEntity(final Entity entity) {
    Objects.requireNonNull(entity);
    this.changedEntities.put((EntityUUID) entity.getId(), entity);
  }

}
