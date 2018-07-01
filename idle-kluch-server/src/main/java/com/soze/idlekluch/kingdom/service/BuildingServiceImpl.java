package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.core.aop.annotations.AuthLog;
import com.soze.idlekluch.core.aop.annotations.Profiled;
import com.soze.idlekluch.game.engine.EntityConverter;
import com.soze.idlekluch.game.engine.EntityUtils;
import com.soze.idlekluch.game.engine.components.*;
import com.soze.idlekluch.game.engine.nodes.Nodes;
import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.game.service.EntityService;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.events.KingdomAddedEvent;
import com.soze.idlekluch.kingdom.exception.*;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.world.entity.TileId;
import com.soze.idlekluch.world.service.ResourceService;
import com.soze.idlekluch.world.service.WorldService;
import com.soze.idlekluch.world.utils.WorldUtils;
import com.soze.klecs.engine.RemovedEntityEvent;
import com.soze.klecs.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.soze.idlekluch.game.engine.EntityUtils.getName;

@Service
public class BuildingServiceImpl implements BuildingService {

  private static final Logger LOG = LoggerFactory.getLogger(BuildingServiceImpl.class);
  private static final String FIRST_BUILDING_ID = "7a4df465-b4c3-4e9f-854a-248988220dfb";

  private final KingdomService kingdomService;
  private final GameEngine gameEngine;
  private final EntityService entityService;
  private final WorldService worldService;
  private final ResourceService resourceService;
  private final EntityConverter entityConverter;

  private final Set<EntityUUID> buildings = ConcurrentHashMap.newKeySet();

  private final Map<String, Object> locks = new ConcurrentHashMap<>();

  @Autowired
  public BuildingServiceImpl(final KingdomService kingdomService,
                             final GameEngine gameEngine,
                             final EntityService entityService,
                             final WorldService worldService,
                             final ResourceService resourceService,
                             final EntityConverter entityConverter) {
    this.kingdomService = Objects.requireNonNull(kingdomService);
    this.gameEngine = Objects.requireNonNull(gameEngine);
    this.entityService = Objects.requireNonNull(entityService);
    this.worldService = Objects.requireNonNull(worldService);
    this.resourceService = Objects.requireNonNull(resourceService);
    this.entityConverter = Objects.requireNonNull(entityConverter);
  }

  @Override
  @AuthLog
  @Profiled
  public Entity buildBuilding(final String owner, final BuildBuildingForm form) {
    Objects.requireNonNull(owner);
    Objects.requireNonNull(form);

    validateKingdom(owner);
    validateTileExists(form);
    validateCost(owner, form);
    validateCollision(form);

    final Entity building = constructBuilding(form);

    //check if there are proper resources in radius
    final ResourceHarvesterComponent resourceHarvesterComponent = building.getComponent(ResourceHarvesterComponent.class);
    if(resourceHarvesterComponent != null) {
      final PhysicsComponent physicsComponent = building.getComponent(PhysicsComponent.class);
      final List<Entity> resources = resourceService.getResourceSourcesInRadius(
        resourceHarvesterComponent.getResource(),
        new Point((int) physicsComponent.getX(), (int) physicsComponent.getY()),
        resourceHarvesterComponent.getRadius()
      );

      if(resources.isEmpty()) {
        throw new NoResourceInRadiusException(
          form.getMessageId(),
          resourceHarvesterComponent.getResource().getName() + " not in radius " + resourceHarvesterComponent.getRadius()
        );
      }
      final Entity closestSource = EntityUtils.getClosestEntity(building, resources).get();
      resourceHarvesterComponent.setSource((EntityUUID) closestSource.getId(), 0);
    }

    final Kingdom kingdom = kingdomService.getUsersKingdom(owner).get();

    final OwnershipComponent ownershipComponent = new OwnershipComponent();
    ownershipComponent.setOwnerId(kingdom.getKingdomId());
    ownershipComponent.setEntityId((EntityUUID) building.getId());
    building.addComponent(ownershipComponent);

    gameEngine.addEntity(building);

    buildings.add((EntityUUID) building.getId());
    LOG.info("[{}] constructed building [{} - {}] at [{}]", owner, form.getBuildingId(), getName(building), new Point(form.getX(), form.getY()));
    return building;
  }

  private void validateCollision(final BuildBuildingForm form) {
    final Point buildingPosition = new Point(form.getX(), form.getY());

    //check for collisions with other buildings
    gameEngine
      .getEntitiesByNode(Nodes.OCCUPY_SPACE)
      .stream()
      .filter(entity -> EntityUtils.intersects(entity, buildingPosition))
      .findFirst()
      .ifPresent(entity -> {
        throw new SpaceAlreadyOccupiedException(form.getMessageId(), "Space is occupied by entityId " + entity.getId() + " named " + getName(entity));
      });
  }

  @Override
  @AuthLog
  public List<Entity> getOwnBuildings(final String owner) {
    Objects.requireNonNull(owner);

    final EntityUUID kingdomId = kingdomService
                                   .getUsersKingdom(owner)
                                   .<UserDoesNotHaveKingdomException>orElseThrow(() -> {
                                     throw new UserDoesNotHaveKingdomException(owner);
                                   })
                                   .getKingdomId();

    return gameEngine
             .getEntitiesByNode(Nodes.OWNERSHIP)
             .stream()
             .filter(entity -> {
               final OwnershipComponent ownershipComponent = entity.getComponent(OwnershipComponent.class);
               return kingdomId.equals(ownershipComponent.getOwnerId());
             })
             .collect(Collectors.toList());
  }

  @Override
  public List<Entity> getAllConstructableBuildings() {
    return entityService.getEntityTemplates()
             .stream()
             .filter(entity -> entity.getComponent(BuildableComponent.class) != null)
             .collect(Collectors.toList());
  }

  @Override
  public List<PersistentEntity> getAllConstructedBuildings() {
    return buildings
             .stream()
             .map(entityService::getEntity)
             .filter(Optional::isPresent)
             .map(Optional::get)
             .collect(Collectors.toList());
  }

  @Override
  @AuthLog
  public void destroyBuilding(final EntityUUID buildingId) {
    Objects.requireNonNull(buildingId);
    throw new IllegalStateException("NOT IMPLEMENTED YET DESTROY BUILDING");
//    buildingRepository.removeBuilding(buildingId);
  }

  @Override
  @EventListener
  public void handleRemovedEntity(final RemovedEntityEvent removedEntityEvent) {
    buildings.remove(removedEntityEvent.getEntity().getId());
  }

  @Override
  @EventListener
  public void handleKingdomAddedEvent(final KingdomAddedEvent kingdomAddedEvent) {
    final Kingdom kingdom = kingdomService.getKingdom(kingdomAddedEvent.getKingdomId()).get();
    final TileId kingdomStartingPoint = kingdom.getStartingPoint();
    final Point startingBuildingPosition = WorldUtils.getTileCenter(kingdomStartingPoint);

    //remove any obstacles for the first building
    gameEngine
      .getEntitiesByNode(Nodes.OCCUPY_SPACE)
      .stream()
      .forEach(entity -> {
        final TileId tileId = WorldUtils.getEntityTileId(entity).get();
        if(tileId.equals(kingdomStartingPoint)) {
          gameEngine.deleteEntity((EntityUUID) entity.getId());
        }
      });

    buildBuilding(
      kingdom.getOwner().getUsername(),
      new BuildBuildingForm(
        EntityUUID.randomId().toString(),
        FIRST_BUILDING_ID,
        startingBuildingPosition.x - 25,
        startingBuildingPosition.y - 27
      ));
  }

  /**
   * Creates an {@link Entity} which will be the building.
   * Does not add or persist this entity.
   */
  private Entity constructBuilding(final BuildBuildingForm form) {
    Objects.requireNonNull(form);

    final Entity buildingTemplate = entityService
                                      .getEntityTemplate(EntityUUID.fromString(form.getBuildingId()))
                                      .<BuildingDoesNotExistException>orElseThrow(() -> {
                                        throw new BuildingDoesNotExistException(form.getMessageId(), form.getBuildingId());
                                      });

    final Entity building = gameEngine.createEmptyEntity();
    entityConverter.copyEntity(buildingTemplate, building);

    final PhysicsComponent physicsComponent = building.getComponent(PhysicsComponent.class);
    physicsComponent.setX(form.getX());
    physicsComponent.setY(form.getY());

    return building;
  }

  private int getBuildingCost(final BuildBuildingForm form) {
    final Entity buildingTemplate = entityService
                                      .getEntityTemplate(EntityUUID.fromString(form.getBuildingId()))
                                      .<BuildingDoesNotExistException>orElseThrow(() -> {
                                        throw new BuildingDoesNotExistException(form.getMessageId(), form.getBuildingId());
                                      });

    return buildingTemplate.getComponent(CostComponent.class).getIdleBucks();
  }

  private Object getLock(final String name) {
    //TODO clear those locks from time to time
    return locks.computeIfAbsent(name, k -> new Object());
  }

  private void validateTileExists(final BuildBuildingForm form) {
    final TileId tileId = WorldUtils.translateCoordinates(form.getX(), form.getY());
    if(!worldService.tileExists(tileId)) {
      throw new SpaceAlreadyOccupiedException(form.getMessageId(), "Space is occupied by void, cannot place outside of tiles");
    }
  }

  private void validateKingdom(final String owner) {
    kingdomService
      .getUsersKingdom(owner)
      .<UserDoesNotHaveKingdomException>orElseThrow(() -> {
        throw new UserDoesNotHaveKingdomException(owner);
      });
  }

  private void validateCost(final String owner, final BuildBuildingForm form) {
    //the locks are on a per-user basis, since one user can only have one kingdom
    synchronized (getLock(owner)) {

      final Kingdom kingdom = kingdomService
                  .getUsersKingdom(owner)
                  .<UserDoesNotHaveKingdomException>orElseThrow(() -> {
                    throw new UserDoesNotHaveKingdomException(owner);
                  });

      final int cost = getBuildingCost(form);
      final long idleBucks = kingdom.getIdleBucks();
      if(idleBucks < cost) {
        throw new CannotAffordBuildingException(form.getMessageId(), form.getBuildingId(), idleBucks, cost);
      }

      kingdom.setIdleBucks(idleBucks - cost);
      kingdomService.updateKingdom(kingdom);
    }
  }

}
