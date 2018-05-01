package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.game.engine.components.GraphicsComponent;
import com.soze.idlekluch.game.engine.components.OwnershipComponent;
import com.soze.idlekluch.game.engine.components.PhysicsComponent;
import com.soze.idlekluch.game.engine.nodes.Nodes;
import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.game.repository.EntityRepository;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.kingdom.dto.BuildingDefinitionDto;
import com.soze.idlekluch.kingdom.dto.WarehouseDefinitionDto;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.exception.BuildingDoesNotExistException;
import com.soze.idlekluch.kingdom.exception.UserDoesNotHaveKingdomException;
import com.soze.idlekluch.user.entity.User;
import com.soze.idlekluch.user.exception.AuthUserDoesNotExistException;
import com.soze.idlekluch.user.service.UserService;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.idlekluch.world.repository.WorldRepository;
import com.soze.klecs.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class BuildingServiceImpl implements BuildingService {

  private static final Logger LOG = LoggerFactory.getLogger(BuildingServiceImpl.class);

  private final UserService userService;
  private final KingdomService kingdomService;

  private final GameEngine gameEngine;
  private final WorldRepository worldRepository;
  private final EntityRepository entityRepository;

  private final Set<EntityUUID> buildings = ConcurrentHashMap.newKeySet();

  //TODO move to repository?
  private final Map<String, BuildingDefinitionDto> buildingDefinitions = new HashMap<>();

  @Value("buildings.json")
  private ClassPathResource buildingData;

  @Autowired
  public BuildingServiceImpl(final UserService userService,
                             final KingdomService kingdomService,
                             final GameEngine gameEngine,
                             final WorldRepository worldRepository,
                             final EntityRepository entityRepository) {
    this.userService = Objects.requireNonNull(userService);
    this.kingdomService = Objects.requireNonNull(kingdomService);
    this.gameEngine = Objects.requireNonNull(gameEngine);
    this.worldRepository = Objects.requireNonNull(worldRepository);
    this.entityRepository = Objects.requireNonNull(entityRepository);
  }

  @Override
  public Map<String, BuildingDefinitionDto> getAllConstructableBuildings() {
    return buildingDefinitions;
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
    final Optional<Kingdom> kingdom = kingdomService.getUsersKingdom(owner);
    if (!kingdom.isPresent()) {
      LOG.info("User [{}] does not have a kingdom", owner);
      throw new UserDoesNotHaveKingdomException(owner);
    }

    //TODO all that in the GameEngine / actually it can be here now
    //check world bounds
    //check for collisions with other buildings
    //check if player's kingdom has enough cash

    final Entity building = constructBuilding(form);
    final OwnershipComponent ownershipComponent = new OwnershipComponent();
    ownershipComponent.setOwnerId(kingdom.get().getKingdomId());
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
  public List<PersistentEntity> getAllConstructedBuildings() {
    return buildings
             .stream()
             .map(entityRepository::getEntity)
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

  private Entity constructBuilding(final BuildBuildingForm form) {
    Objects.requireNonNull(form);

    final BuildingDefinitionDto buildingDefinition = buildingDefinitions.get(form.getBuildingId());
    if (buildingDefinition == null) {
      throw new BuildingDoesNotExistException(form.getBuildingId());
    }

    final Entity building = constructBuilding(buildingDefinition);

    if (building == null) {
      throw new IllegalStateException("Building cannot be null");
    }

    final PhysicsComponent physicsComponent = (PhysicsComponent) building.getComponent(PhysicsComponent.class);
    physicsComponent.setX(form.getX());
    physicsComponent.setY(form.getY());

    return building;
  }

  private Entity constructBuilding(final BuildingDefinitionDto buildingDefinition) {
    Objects.requireNonNull(buildingDefinition);

    switch (buildingDefinition.getType()) {
      case WAREHOUSE:
        return constructWarehouse((WarehouseDefinitionDto) buildingDefinition);
    }

    throw new IllegalStateException("Ops, could not construct building " + buildingDefinition.getId());
  }

  private Entity constructWarehouse(final WarehouseDefinitionDto warehouseDefinition) {
    Objects.requireNonNull(warehouseDefinition);

    final Entity entity = gameEngine.createEmptyEntity();

    final PhysicsComponent physicsComponent = new PhysicsComponent();
    physicsComponent.setWidth(warehouseDefinition.getWidth());
    physicsComponent.setHeight(warehouseDefinition.getHeight());
    physicsComponent.setEntityId((EntityUUID) entity.getId());
    entity.addComponent(physicsComponent);

    final GraphicsComponent graphicsComponent = new GraphicsComponent();
    graphicsComponent.setAsset(warehouseDefinition.getAsset());
    graphicsComponent.setEntityId((EntityUUID) entity.getId());
    entity.addComponent(graphicsComponent);

    return entity;
  }

}
