package com.soze.idlekluch.game.engine;

import com.soze.idlekluch.game.engine.components.PhysicsComponent;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.kingdom.entity.Building;
import com.soze.klecs.entity.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Converts database Entities to ECS Entities.
 */
@Service
public class EntityConverter {

  private final GameEngine gameEngine;

  @Autowired
  public EntityConverter(GameEngine gameEngine) {
    this.gameEngine = Objects.requireNonNull(gameEngine);
  }

  public Entity convert(final Building building) {
    Objects.requireNonNull(building);

    final Entity entity = gameEngine.createEmptyEntity();

    final PhysicsComponent physicsComponent = new PhysicsComponent();
    physicsComponent.setX(building.getX());
    physicsComponent.setY(building.getY());

    return entity;
  }

}
