package com.soze.idlekluch.world.service;

import com.soze.idlekluch.core.aop.annotations.Profiled;
import com.soze.idlekluch.game.engine.EntityConverter;
import com.soze.idlekluch.game.engine.EntityUtils;
import com.soze.idlekluch.game.engine.components.PhysicsComponent;
import com.soze.idlekluch.game.engine.components.ResourceSourceComponent;
import com.soze.idlekluch.game.engine.nodes.Nodes;
import com.soze.idlekluch.game.service.EntityService;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.idlekluch.core.utils.CommonUtils;
import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.events.WorldChunkCreatedEvent;
import com.soze.idlekluch.world.utils.WorldUtils;
import com.soze.klecs.entity.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ResourceServiceImpl implements ResourceService {

  private final float resourceDensity = 0.15f;

  private final EntityService entityService;
  private final GameEngine gameEngine;
  private final EntityConverter entityConverter;

  @Autowired
  public ResourceServiceImpl(final EntityService entityService,
                             final GameEngine gameEngine,
                             final EntityConverter entityConverter) {

    this.entityService = Objects.requireNonNull(entityService);
    this.gameEngine = Objects.requireNonNull(gameEngine);
    this.entityConverter = entityConverter;
  }

  @Override
  @Profiled
  public List<Entity> getAllResourceEntityTemplates() {
    return entityService
             .getEntityTemplates()
             .stream()
             .filter(entity -> entity.getComponent(ResourceSourceComponent.class) != null)
             .collect(Collectors.toList());
  }

  @Override
  public List<Entity> getResourceEntityTemplates(final Resource resource) {
    Objects.requireNonNull(resource);

    return getAllResourceEntityTemplates()
             .stream()
             .filter(entity -> {
               final ResourceSourceComponent resourceSourceComponent = entity.getComponent(ResourceSourceComponent.class);
               return resource.equals(resourceSourceComponent.getResource());
             })
             .collect(Collectors.toList());
  }

  @Override
  public List<Entity> getResourceEntityTemplates(final String resourceName) {
    Objects.requireNonNull(resourceName);

    return getAllResourceEntityTemplates()
             .stream()
             .filter(entity -> {
               final ResourceSourceComponent resourceSourceComponent = entity.getComponent(ResourceSourceComponent.class);
               return resourceSourceComponent.getResource().getName().equalsIgnoreCase(resourceName);
             })
             .collect(Collectors.toList());
  }

  @Override
  public List<Entity> getAllResourceSources() {
    return gameEngine.getEntitiesByNode(Nodes.RESOURCE_SOURCE);
  }

  @Override
  public List<Entity> getAllResourceSources(final Resource resource) {
    return getAllResourceSources()
             .stream()
             .filter(source -> {
               final ResourceSourceComponent resourceSourceComponent = source.getComponent(ResourceSourceComponent.class);
               return resource.equals(resourceSourceComponent.getResource());
             })
             .collect(Collectors.toList());
  }

  @Override
  @EventListener
  @Profiled
  public void handleWorldChunkCreatedEvent(final WorldChunkCreatedEvent worldChunkCreatedEvent) {
    final List<Entity> resourceSources = getAllResourceEntityTemplates();

    if(resourceSources.isEmpty()) {
      return;
    }

    worldChunkCreatedEvent
      .getTiles()
      .stream()
      .filter(tile -> Math.random() < resourceDensity)
      .forEach(tile -> {
        final Entity randomResourceSourceTemplate = CommonUtils.getRandomElement(resourceSources).get();
        final Entity randomResourceSource = gameEngine.createEmptyEntity();
        entityConverter.copyEntity(randomResourceSourceTemplate, randomResourceSource);
        final PhysicsComponent physicsComponent = randomResourceSource.getComponent(PhysicsComponent.class);
        final Point position = getResourcePosition(tile, physicsComponent.getWidth(), physicsComponent.getHeight());
        physicsComponent.setX(position.x);
        physicsComponent.setY(position.y);

        gameEngine.addEntity(randomResourceSource);
      });

  }

  @Override
  public List<Entity> getResourceSourcesInRadius(final Resource resource, final Point center, final float radius) {
    return getAllResourceSources(resource)
             .stream()
             .filter(source -> EntityUtils.distance(source, center) <= radius)
             .collect(Collectors.toList());
  }

  /**
   * Finds a random position within the given tile for a resource.
   */
  private Point getResourcePosition(final Tile tile, final int resourceWidth, final int resourceHeight) {
    final Point position = WorldUtils.getTileCenter(tile);
    position.x -= CommonUtils.randomNumber(0, resourceWidth);
    position.y -= CommonUtils.randomNumber(0, resourceHeight);
    return position;
  }
}
