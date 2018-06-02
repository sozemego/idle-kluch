package com.soze.idlekluch.world.service;

import com.soze.idlekluch.game.engine.EntityConverter;
import com.soze.idlekluch.game.engine.components.PhysicsComponent;
import com.soze.idlekluch.game.engine.components.ResourceSourceComponent;
import com.soze.idlekluch.game.service.EntityService;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.utils.CommonUtils;
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
    this.entityConverter = Objects.requireNonNull(entityConverter);
  }

  @Override
  @EventListener
  public void handleWorldChunkCreatedEvent(final WorldChunkCreatedEvent worldChunkCreatedEvent) {
    final List<Entity> resourceSources = entityService
                                           .getEntityTemplates()
                                           .stream()
                                           .filter(entity -> entity.getComponent(ResourceSourceComponent.class) != null)
                                           .collect(Collectors.toList());

    for (final Tile tile : worldChunkCreatedEvent.getTiles()) {
      if (Math.random() < resourceDensity) {
        final Entity randomResourceSourceTemplate = CommonUtils.getRandomElement(resourceSources).get();
        final Entity randomResourceSource = gameEngine.createEmptyEntity();
        entityService.copyEntity(randomResourceSourceTemplate, randomResourceSource);
        final PhysicsComponent physicsComponent = randomResourceSource.getComponent(PhysicsComponent.class);
        final Point position = WorldUtils.getTileCenter(tile);
        physicsComponent.setX(position.x);
        physicsComponent.setY(position.y);

        gameEngine.addEntity(randomResourceSource);
      }
    }

  }
}
