package com.soze.idlekluch.game.engine;

import com.soze.idlekluch.game.engine.components.BaseComponent;
import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.game.message.EntityMessage;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.kingdom.service.BuildingService;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.klecs.entity.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Converts database Entities to ECS Entities.
 */
@Service
public class EntityConverter {

  private final GameEngine gameEngine;
  private final BuildingService buildingService;

  @Autowired
  public EntityConverter(final GameEngine gameEngine, final BuildingService buildingService) {
    this.gameEngine = Objects.requireNonNull(gameEngine);
    this.buildingService = Objects.requireNonNull(buildingService);
  }

  public Entity convert(final PersistentEntity entity) {
    Objects.requireNonNull(entity);


    return null;
  }

//  public Entity convert(final Building building) {
//    Objects.requireNonNull(building);
//
//    final Entity entity = gameEngine.createEmptyEntity();
//
//    final PhysicsComponent physicsComponent = new PhysicsComponent();
//    physicsComponent.setX(building.getX());
//    physicsComponent.setY(building.getY());
//
//    final BuildingDefinitionDto buildingDefinitionDto = buildingService.getAllConstructableBuildings().get(building.getDefinitionId());
//    physicsComponent.setWidth(buildingDefinitionDto.getWidth());
//    physicsComponent.setHeight(buildingDefinitionDto.getHeight());
//    entity.addComponent(physicsComponent);
//
//    final GraphicsComponent graphicsComponent = new GraphicsComponent(buildingDefinitionDto.getAsset());
//    entity.addComponent(graphicsComponent);
//
//    return entity;
//  }
//
//  public Entity convert(final Forest forest) {
//    Objects.requireNonNull(forest);
//
//    final Entity entity = gameEngine.createEmptyEntity();
//
//    final PhysicsComponent physicsComponent = new PhysicsComponent();
//    physicsComponent.setX(forest.getX());
//    physicsComponent.setY(forest.getY());
//    entity.addComponent(physicsComponent);
//
//    final GraphicsComponent graphicsComponent = new GraphicsComponent(forestService.getForestAsset(forest.getDefinitionId()));
//    entity.addComponent(graphicsComponent);
//
//    return entity;
//  }

  public EntityMessage toMessage(final Entity entity) {
    final List<BaseComponent> components = entity.getAllComponents(BaseComponent.class);
    return new EntityMessage((EntityUUID)entity.getId(), components);
  }

}
