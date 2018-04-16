package com.soze.idlekluch.game.engine;

import com.soze.idlekluch.game.engine.components.BaseComponent;
import com.soze.idlekluch.game.engine.components.GraphicsComponent;
import com.soze.idlekluch.game.engine.components.PhysicsComponent;
import com.soze.idlekluch.game.message.EntityMessage;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.kingdom.dto.BuildingDefinitionDto;
import com.soze.idlekluch.kingdom.entity.Building;
import com.soze.idlekluch.kingdom.service.BuildingService;
import com.soze.idlekluch.world.entity.Tree;
import com.soze.idlekluch.world.service.TreeService;
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
  private final TreeService treeService;

  @Autowired
  public EntityConverter(final GameEngine gameEngine, final BuildingService buildingService, final TreeService treeService) {
    this.gameEngine = Objects.requireNonNull(gameEngine);
    this.buildingService = Objects.requireNonNull(buildingService);
    this.treeService = Objects.requireNonNull(treeService);
  }

  public Entity convert(final Building building) {
    Objects.requireNonNull(building);

    final Entity entity = gameEngine.createEmptyEntity();

    final PhysicsComponent physicsComponent = new PhysicsComponent();
    physicsComponent.setX(building.getX());
    physicsComponent.setY(building.getY());

    final BuildingDefinitionDto buildingDefinitionDto = buildingService.getAllConstructableBuildings().get(building.getDefinitionId());
    physicsComponent.setWidth(buildingDefinitionDto.getWidth());
    physicsComponent.setHeight(buildingDefinitionDto.getHeight());
    entity.addComponent(physicsComponent);

    final GraphicsComponent graphicsComponent = new GraphicsComponent(buildingDefinitionDto.getAsset());
    entity.addComponent(graphicsComponent);

    return entity;
  }

  public Entity convert(final Tree tree) {
    Objects.requireNonNull(tree);

    final Entity entity = gameEngine.createEmptyEntity();

    final PhysicsComponent physicsComponent = new PhysicsComponent();
    physicsComponent.setX(tree.getX());
    physicsComponent.setY(tree.getY());
    entity.addComponent(physicsComponent);

    final GraphicsComponent graphicsComponent = new GraphicsComponent(treeService.getTreeAsset(tree.getDefinitionId()));
    entity.addComponent(graphicsComponent);

    return entity;
  }

  public EntityMessage toMessage(final Entity entity) {
    final List<BaseComponent> components = entity.getAllComponents();
    return new EntityMessage(entity.getId(), components);
  }

}
