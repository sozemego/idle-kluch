package com.soze.idlekluch.world.service;

import com.soze.idlekluch.core.aop.annotations.Profiled;
import com.soze.idlekluch.core.event.AppStartedEvent;
import com.soze.idlekluch.game.engine.EntityConverter;
import com.soze.idlekluch.game.engine.EntityUtils;
import com.soze.idlekluch.game.engine.components.PhysicsComponent;
import com.soze.idlekluch.game.engine.components.ResourceHarvesterComponent;
import com.soze.idlekluch.game.engine.components.ResourceSourceComponent;
import com.soze.idlekluch.game.engine.nodes.Nodes;
import com.soze.idlekluch.game.service.EntityService;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.idlekluch.core.utils.CommonUtils;
import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.events.WorldChunkCreatedEvent;
import com.soze.idlekluch.world.repository.WorldRepository;
import com.soze.idlekluch.world.utils.WorldUtils;
import com.soze.klecs.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import sun.util.locale.provider.LocaleServiceProviderPool.LocalizedObjectGetter;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ResourceServiceImpl implements ResourceService {

  private static final Logger LOG = LoggerFactory.getLogger(ResourceServiceImpl.class);

  private final float resourceDensity = 0.15f;

  private final EntityService entityService;
  private final GameEngine gameEngine;
  private final EntityConverter entityConverter;
  private final WorldRepository worldRepository;

  @Autowired
  public ResourceServiceImpl(final EntityService entityService,
                             final GameEngine gameEngine,
                             final EntityConverter entityConverter,
                             final WorldRepository worldRepository) {

    this.entityService = Objects.requireNonNull(entityService);
    this.gameEngine = Objects.requireNonNull(gameEngine);
    this.entityConverter = Objects.requireNonNull(entityConverter);
    this.worldRepository = Objects.requireNonNull(worldRepository);
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

  @Override
  @Profiled
  public void handleAppStartedEvent(final AppStartedEvent event) {
    attachResourceHarvestersToSources();
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

  /**
   * For each entity which is a resource source, retrieves resource harvesters in range.
   * For each resource harvester in range, increments the number of harvesters.
   */
  private void attachResourceHarvestersToSources() {
    LOG.info("Attaching resource harvesters to resource sources and vice versa");
    //1. Get all possible resources
    final List<Resource> resources = worldRepository.getAllAvailableResources();
    //2. Get all possible entities which are resource sources
    final List<Entity> resourceSources = gameEngine.getEntitiesByNode(Nodes.RESOURCE_SOURCE);
    //3. Get all possible entities which are resource harvesters
    final List<Entity> resourceHarvesters = gameEngine.getEntitiesByNode(Nodes.HARVESTER);
    //3. Create a Resource/List<Entity> (resource source) map
    final Map<Resource, List<Entity>> resourceSourcesMap = resources
                                                              .stream()
                                                              .collect(Collectors.toMap(
                                                                Function.identity(),
                                                                (resource) -> findResourceSources(resource, resourceSources))
                                                              );
    //4. Create a Resource/List<Entity> (resource harvester) map
    final Map<Resource, List<Entity>> resourceHarvestersMap = resources
                                                              .stream()
                                                              .collect(Collectors.toMap(
                                                                Function.identity(),
                                                                (resource) -> findResourceHarvesters(resource, resourceHarvesters))
                                                              );
    //5. Next we will match each resource source with resource harvesters
    for (Map.Entry<Resource, List<Entity>> entry: resourceSourcesMap.entrySet()) {
      final Resource resource = entry.getKey();
      final List<Entity> sources = entry.getValue();
      final List<Entity> harvesters = resourceHarvestersMap.get(resource);
      sources.forEach(source -> {
        final List<Entity> inRangeHarvesters = findAllHarvestersInRange(source, harvesters);
        inRangeHarvesters.forEach(harvester -> {
          LOG.debug("Attaching source [{}] to harvester [{}]", source, harvester);
          addSource(harvester, source);
          LOG.debug("Attaching harvester [{}] to source [{}]", harvester, source);
          addHarvester(source, harvester);
        });
      });
    }
  }

  private List<Entity> findResourceSources(final Resource resource, final List<Entity> resourceSources) {
    return resourceSources
             .stream()
             .filter(entity -> {
               final ResourceSourceComponent resourceSourceComponent = entity.getComponent(ResourceSourceComponent.class);
               return resourceSourceComponent.getResource().equals(resource);
             })
             .collect(Collectors.toList());
  }

  private List<Entity> findResourceHarvesters(final Resource resource, final List<Entity> resourceHarvesters) {
    return resourceHarvesters
             .stream()
             .filter(entity -> {
               final ResourceHarvesterComponent resourceHarvesterComponent = entity.getComponent(ResourceHarvesterComponent.class);
               return resourceHarvesterComponent.getResource().equals(resource);
             })
             .collect(Collectors.toList());
  }

  final List<Entity> findAllHarvestersInRange(final Entity source, final List<Entity> harvesters) {
    return harvesters
             .stream()
             .filter(harvester -> {
               final ResourceHarvesterComponent resourceHarvesterComponent = harvester.getComponent(ResourceHarvesterComponent.class);
               return EntityUtils.distance(source, harvester) <= resourceHarvesterComponent.getRadius();
             })
             .collect(Collectors.toList());
  }

  final void addHarvester(final Entity source, final Entity harvester) {
    final ResourceSourceComponent resourceSourceComponent = source.getComponent(ResourceSourceComponent.class);
    resourceSourceComponent.addHarvester(harvester);
  }

  final void addSource(final Entity harvester, final Entity source) {
    final ResourceHarvesterComponent resourceHarvesterComponent = harvester.getComponent(ResourceHarvesterComponent.class);
    resourceHarvesterComponent.addSource(source);
  }

}
