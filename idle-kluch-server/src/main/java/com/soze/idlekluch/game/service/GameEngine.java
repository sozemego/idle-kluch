package com.soze.idlekluch.game.service;

import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.klecs.entity.Entity;

import java.util.List;

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


}
