package com.soze.idlekluch.game.service;

import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.klecs.entity.Entity;

import java.util.List;
import java.util.Optional;

/**
 * Contains the ECS for the game.
 */
public interface GameEngine {

  /**
   * Updates game state with given delta
   */
  void update(float delta);

  Entity createEmptyEntity();

  /**
   * Adds an entity to the engine.
   */
  void addEntity(final Entity<EntityUUID> entity);

  List<Entity<EntityUUID>> getAllEntities();

  Optional<Entity<EntityUUID>> getEntity(final EntityUUID entityId);


}
