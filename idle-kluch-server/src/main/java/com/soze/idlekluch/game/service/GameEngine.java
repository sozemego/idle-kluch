package com.soze.idlekluch.game.service;

import com.soze.klecs.entity.Entity;

/**
 * Contains the ECS for the game.
 */
public interface GameEngine {

  /**
   * Updates game state with given delta
   */
  void update(float delta);

  /**
   * Adds an entity to the engine.
   */
  void addEntity(final Entity entity);


}
