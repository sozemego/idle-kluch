package com.soze.idlekluch.world.utils;

import com.soze.idlekluch.game.engine.components.PhysicsComponent;
import com.soze.idlekluch.world.entity.TileId;
import com.soze.idlekluch.world.service.WorldService;
import com.soze.klecs.entity.Entity;

import java.util.Objects;
import java.util.Optional;

public final class WorldUtils {

  private WorldUtils() {
    throw new IllegalArgumentException("Cannot instantiate WorldUtils");
  }

  /**
   * If the entity has {@link PhysicsComponent}, translates the position into {@link TileId}
   * this entity is on.
   * @return
   */
  public static Optional<TileId> getEntityTileId(final Entity entity) {
    Objects.requireNonNull(entity);

    final PhysicsComponent physicsComponent = entity.getComponent(PhysicsComponent.class);
    if(physicsComponent == null) {
      return Optional.empty();
    }

    final float x = physicsComponent.getX();
    final float y = physicsComponent.getY();

    return Optional.of(new TileId((int) x / WorldService.TILE_SIZE, (int) y / WorldService.TILE_SIZE));
  }

}