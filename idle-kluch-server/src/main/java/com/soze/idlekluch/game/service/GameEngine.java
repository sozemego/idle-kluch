package com.soze.idlekluch.game.service;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.klecs.entity.Entity;
import com.soze.klecs.node.Node;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Contains the ECS for the game.
 */
public interface GameEngine {

  /**
   * Updates game state with given delta
   */
  void update(float delta);

  /**
   * Starts the engine. Has no effect if engine is already started.
   */
  void start();

  /**
   * Stops the engine. Has no effect if engine is not running.
   */
  void stop();

  boolean isPaused();

  void setDelta(final float delta);

  Entity createEmptyEntity();

  Entity createEmptyEntity(EntityUUID entityId);

  /**
   * Adds an entity to the engine.
   */
  void addEntity(Entity entity);

  List<Entity> getAllEntities();

  Collection<Entity> getAllEntitiesCollection();

  Set<Entity> getAllChangedEntities();

  List<Entity> getEntitiesByNode(Node node);

  Optional<Entity> getEntity(EntityUUID entityId);

  void deleteEntity(EntityUUID entityId);

  /**
   * Removes all entities.
   */
  void reset();

  /**
   * Accepts an action to be run after the engine has updated.
   */
  void addAction(Runnable action);

}
