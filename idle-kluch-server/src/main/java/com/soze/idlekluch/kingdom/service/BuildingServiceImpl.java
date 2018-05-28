package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.aop.annotations.Authorized;
import com.soze.idlekluch.aop.annotations.Profiled;
import com.soze.idlekluch.game.engine.EntityUtils;
import com.soze.idlekluch.game.engine.components.BuildableComponent;
import com.soze.idlekluch.game.engine.components.CostComponent;
import com.soze.idlekluch.game.engine.components.OwnershipComponent;
import com.soze.idlekluch.game.engine.components.PhysicsComponent;
import com.soze.idlekluch.game.engine.nodes.Nodes;
import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.game.service.EntityService;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.exception.BuildingDoesNotExistException;
import com.soze.idlekluch.kingdom.exception.CannotAffordBuildingException;
import com.soze.idlekluch.kingdom.exception.SpaceAlreadyOccupiedException;
import com.soze.idlekluch.kingdom.exception.UserDoesNotHaveKingdomException;
import com.soze.idlekluch.user.exception.AuthUserDoesNotExistException;
import com.soze.idlekluch.user.service.UserService;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.idlekluch.world.entity.TileId;
import com.soze.idlekluch.world.service.WorldService;
import com.soze.idlekluch.world.utils.WorldUtils;
import com.soze.klecs.engine.RemovedEntityEvent;
import com.soze.klecs.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class BuildingServiceImpl implements BuildingService {

  private static final Logger LOG = LoggerFactory.getLogger(BuildingServiceImpl.class);

  private final UserService userService;
  private final KingdomService kingdomService;

  private final GameEngine gameEngine;
  private final EntityService entityService;
  private final WorldService worldService;

  private final Set<EntityUUID> buildings = ConcurrentHashMap.newKeySet();

  private final Map<String, Object> locks = new ConcurrentHashMap<>();

  @Autowired
  public BuildingServiceImpl(final UserService userService,
                             final KingdomService kingdomService,
                             final GameEngine gameEngine,
                             final EntityService entityService,
                             final WorldService worldService) {
    this.userService = Objects.requireNonNull(userService);
    this.kingdomService = Objects.requireNonNull(kingdomService);
    this.gameEngine = Objects.requireNonNull(gameEngine);
    this.entityService = Objects.requireNonNull(entityService);
    this.worldService = Objects.requireNonNull(worldService);
  }

  @Override
  @Authorized
  @Profiled
  public Entity buildBuilding(final String owner, final BuildBuildingForm form) {
    Objects.requireNonNull(owner);
    Objects.requireNonNull(form);

    validateUser(owner);
    validateKingdom(owner);
    validateTileExists(form);
    validateCost(owner, form);
    validateCollision(form);

    final Entity building = constructBuilding(form);
    final Kingdom kingdom = kingdomService.getUsersKingdom(owner).get();

    final OwnershipComponent ownershipComponent = new OwnershipComponent();
    ownershipComponent.setOwnerId(kingdom.getKingdomId());
    ownershipComponent.setEntityId((EntityUUID) building.getId());
    building.addComponent(ownershipComponent);

    gameEngine.addEntity(building);

    buildings.add((EntityUUID) building.getId());
    LOG.info("[{}] constructed building [{}] at [{}]", owner, form.getBuildingId(), new Point(form.getX(), form.getY()));
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
        throw new SpaceAlreadyOccupiedException(form.getMessageId(), "Space is occupied by entityId: " + entity.getId());
      });
  }

  @Override
  @Authorized
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
  @Authorized
  public void destroyBuilding(final EntityUUID buildingId) {
    Objects.requireNonNull(buildingId);
    throw new IllegalStateException("NOT IMPLEMENTED YET DESTROY BUILDING");
//    buildingRepository.removeBuilding(buildingId);
  }

  @Override
  public void handleRemovedEntity(final RemovedEntityEvent removedEntityEvent) {
    Objects.requireNonNull(removedEntityEvent);
    buildings.remove(removedEntityEvent.getEntity().getId());
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
    entityService.copyEntity(buildingTemplate, building);

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

  private void validateUser(final String owner) {
    userService
      .getUserByUsername(owner)
      .<AuthUserDoesNotExistException>orElseThrow(() -> {
        throw new AuthUserDoesNotExistException(owner);
      });
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
