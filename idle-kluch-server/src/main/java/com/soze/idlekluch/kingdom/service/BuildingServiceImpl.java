package com.soze.idlekluch.kingdom.service;

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
import com.soze.idlekluch.user.entity.User;
import com.soze.idlekluch.user.exception.AuthUserDoesNotExistException;
import com.soze.idlekluch.user.service.UserService;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.klecs.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class BuildingServiceImpl implements BuildingService {

  private static final Logger LOG = LoggerFactory.getLogger(BuildingServiceImpl.class);

  private final UserService userService;
  private final KingdomService kingdomService;

  private final GameEngine gameEngine;
  private final EntityService entityService;

  private final Set<EntityUUID> buildings = ConcurrentHashMap.newKeySet();

  @Autowired
  public BuildingServiceImpl(final UserService userService,
                             final KingdomService kingdomService,
                             final GameEngine gameEngine,
                             final EntityService entityService) {
    this.userService = Objects.requireNonNull(userService);
    this.kingdomService = Objects.requireNonNull(kingdomService);
    this.gameEngine = Objects.requireNonNull(gameEngine);
    this.entityService = Objects.requireNonNull(entityService);
  }

  @Override
  public Entity buildBuilding(final String owner, final BuildBuildingForm form) {
    Objects.requireNonNull(owner);
    Objects.requireNonNull(form);

    LOG.info("User [{}] is trying to place a building [{}]", owner, form);

    //check if owner exists, just in case
    final Optional<User> userOptional = userService.getUserByUsername(owner);
    if (!userOptional.isPresent()) {
      LOG.info("User [{}] does not exist, cannot place building [{}]", owner, form);
      throw new AuthUserDoesNotExistException(owner);
    }

//    final User user = userOptional.get();

    //now get user's kingdom
    final Optional<Kingdom> kingdomOptional = kingdomService.getUsersKingdom(owner);
    if (!kingdomOptional.isPresent()) {
      LOG.info("User [{}] does not have a kingdom", owner);
      throw new UserDoesNotHaveKingdomException(owner);
    }

    final Kingdom kingdom = kingdomOptional.get();

    //TODO check world bounds

    final Entity building = constructBuilding(form);

    //check for collisions with other buildings
    final Optional<Entity> collidedWith = gameEngine.getEntitiesByNode(Nodes.OCCUPY_SPACE)
                                                .stream()
                                                .filter(entity -> {
                                                  final PhysicsComponent physicsComponent = entity.getComponent(PhysicsComponent.class);
                                                  return EntityUtils.doesCollide(building.getComponent(PhysicsComponent.class), physicsComponent);
                                                })
                                                .findFirst();

    if(collidedWith.isPresent()) {
      LOG.info("Cannot build building [{}], another building is on its place", form.getBuildingId());
      throw new SpaceAlreadyOccupiedException(form.getMessageId());
    }

    //check if player's kingdom has enough cash
    final CostComponent costComponent = building.getComponent(CostComponent.class);
    final long idleBucks = kingdom.getIdleBucks();
    if(idleBucks < costComponent.getIdleBucks()) {
      LOG.info("[{}] cannot afford building [{}]. Cost is [{}], money is [{}]", owner, form.getBuildingId(), costComponent.getIdleBucks(), idleBucks);
      throw new CannotAffordBuildingException(form.getBuildingId(), idleBucks, costComponent.getIdleBucks());
    }

    kingdom.setIdleBucks(idleBucks - costComponent.getIdleBucks());
    kingdomService.updateKingdom(kingdom);

    final OwnershipComponent ownershipComponent = new OwnershipComponent();
    ownershipComponent.setOwnerId(kingdom.getKingdomId());
    ownershipComponent.setEntityId((EntityUUID) building.getId());
    building.addComponent(ownershipComponent);

    gameEngine.addEntity(building);

    buildings.add((EntityUUID) building.getId());

    return building;
  }

  @Override
  public List<Entity> getOwnBuildings(final String owner) {
    Objects.requireNonNull(owner);

    final Optional<Kingdom> kingdom = kingdomService.getUsersKingdom(owner);
    if (!kingdom.isPresent()) {
      LOG.info("User [{}] does not have a kingdom", owner);
      throw new UserDoesNotHaveKingdomException(owner);
    }

    final EntityUUID kingdomId = kingdom.get().getKingdomId();

    return gameEngine.getEntitiesByNode(Nodes.OWNERSHIP)
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
  public void destroyBuilding(final EntityUUID buildingId) {
    Objects.requireNonNull(buildingId);
    throw new IllegalStateException("NOT IMPLEMENTED YET DESTROY BUILDING");
//    buildingRepository.removeBuilding(buildingId);
  }

  /**
   * Creates an {@link Entity} which will be the building.
   * Does not add or persist this entity.
   */
  private Entity constructBuilding(final BuildBuildingForm form) {
    Objects.requireNonNull(form);

    final Optional<Entity> templateOptional = entityService.getEntityTemplate(EntityUUID.fromString(form.getBuildingId()));

    if(!templateOptional.isPresent()) {
      throw new BuildingDoesNotExistException(form.getBuildingId());
    }

    final Entity buildingTemplate = templateOptional.get();
    final Entity building = gameEngine.createEmptyEntity();
    entityService.copyEntity(buildingTemplate, building);

    final PhysicsComponent physicsComponent = building.getComponent(PhysicsComponent.class);
    physicsComponent.setX(form.getX());
    physicsComponent.setY(form.getY());

    return building;
  }

}
