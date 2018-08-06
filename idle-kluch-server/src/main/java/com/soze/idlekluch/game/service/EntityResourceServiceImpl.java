package com.soze.idlekluch.game.service;

import com.soze.idlekluch.core.aop.annotations.Profiled;
import com.soze.idlekluch.core.exception.EntityDoesNotExistException;
import com.soze.idlekluch.core.utils.CommonUtils;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.EntityConverter;
import com.soze.idlekluch.game.engine.EntityUtils;
import com.soze.idlekluch.game.engine.components.PhysicsComponent;
import com.soze.idlekluch.game.engine.components.ResourceSourceComponent;
import com.soze.idlekluch.game.engine.nodes.Nodes;
import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.events.WorldChunkCreatedEvent;
import com.soze.idlekluch.world.service.ResourceService;
import com.soze.idlekluch.world.service.ResourceServiceImpl;
import com.soze.idlekluch.world.utils.WorldUtils;
import com.soze.klecs.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EntityResourceServiceImpl implements EntityResourceService {

  private static final Logger LOG = LoggerFactory.getLogger(ResourceServiceImpl.class);

  private final float resourceDensity = 0.15f;

  private final EntityService entityService;
  private final ResourceService resourceService;
  private final GameEngine gameEngine;
  private final EntityConverter entityConverter;

  @Autowired
  public EntityResourceServiceImpl(final EntityService entityService,
                                   final ResourceService resourceService,
                                   final GameEngine gameEngine,
                                   final EntityConverter entityConverter) {
    this.entityService = Objects.requireNonNull(entityService);
    this.entityConverter = entityConverter;
    this.resourceService = Objects.requireNonNull(resourceService);
    this.gameEngine = Objects.requireNonNull(gameEngine);
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
               return resource.getResourceId().equals(resourceSourceComponent.getResourceId());
             })
             .collect(Collectors.toList());
  }

  @Override
  public List<Entity> getResourceEntityTemplates(final String resourceName) {
    Objects.requireNonNull(resourceName);
    final Resource resource = resourceService.getResource(resourceName).orElseThrow(() -> new IllegalArgumentException());

    return getAllResourceEntityTemplates()
             .stream()
             .filter(entity -> {
               final ResourceSourceComponent resourceSourceComponent = entity.getComponent(ResourceSourceComponent.class);
               return resourceSourceComponent.getResourceId().equals(resource.getResourceId());
             })
             .collect(Collectors.toList());
  }

  @Override
  public List<Entity> getAllResourceSources() {
    return gameEngine.getEntitiesByNode(Nodes.RESOURCE_SOURCE);
  }

  @Override
  public List<Entity> getAllResourceSources(final Resource resource) {
    Objects.requireNonNull(resource);
    return getAllResourceSources()
             .stream()
             .filter(source -> {
               final ResourceSourceComponent resourceSourceComponent = source.getComponent(ResourceSourceComponent.class);
               return resource.getResourceId().equals(resourceSourceComponent.getResourceId());
             })
             .collect(Collectors.toList());
  }

  @Override
  @EventListener
  @Profiled
  public void handleWorldChunkCreatedEvent(final WorldChunkCreatedEvent worldChunkCreatedEvent) {
    final List<Entity> resourceSources = getAllResourceEntityTemplates();

    if (resourceSources.isEmpty()) {
      return;
    }

    worldChunkCreatedEvent
      .getTiles()
      .stream()
      .filter(tile -> Math.random() < resourceDensity)
      .forEach(tile -> {
        final Entity randomResourceSourceTemplate = CommonUtils.getRandomElement(resourceSources).get();
        final PhysicsComponent templatePhysicsComponent = randomResourceSourceTemplate.getComponent(PhysicsComponent.class);
        final Point position = getResourcePosition(tile, templatePhysicsComponent.getWidth(), templatePhysicsComponent.getHeight());
        placeResourceSource((EntityUUID) randomResourceSourceTemplate.getId(), position);
      });
  }

  @Override
  public List<Entity> getResourceSourcesInRadius(final Resource resource, final Point center, final int radius) {
    return getAllResourceSources(resource)
             .stream()
             .filter(source -> EntityUtils.distance(source, center) <= radius)
             .collect(Collectors.toList());
  }

  @Override
  public List<Entity> getResourceSourcesInRadius(final EntityUUID resourceId, final Point center, final int radius) {
    final Resource resource = resourceService.getResource(resourceId).orElseThrow(() -> new IllegalArgumentException());
    return getResourceSourcesInRadius(resource, center, radius);
  }

  @Override
  public Entity placeResourceSource(final EntityUUID entityId, final Point position) {
    final Entity resourceSourceTemplate = entityService
                                            .getEntityTemplate(entityId)
                                            .orElseThrow(() -> new EntityDoesNotExistException("No template with id " + entityId, Entity.class));

    final Entity resourceSource = gameEngine.createEmptyEntity();
    entityConverter.copyEntity(resourceSourceTemplate, resourceSource);
    final PhysicsComponent physicsComponent = resourceSource.getComponent(PhysicsComponent.class);
    physicsComponent.setX(position.x);
    physicsComponent.setY(position.y);

    gameEngine.addEntity(resourceSource);
    return resourceSource;
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
