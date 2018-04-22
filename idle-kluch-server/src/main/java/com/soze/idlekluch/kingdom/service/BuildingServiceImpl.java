package com.soze.idlekluch.kingdom.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.kingdom.dto.BuildingDefinitionDto;
import com.soze.idlekluch.kingdom.dto.BuildingDto.BuildingType;
import com.soze.idlekluch.kingdom.dto.WarehouseDefinitionDto;
import com.soze.idlekluch.kingdom.entity.*;
import com.soze.idlekluch.kingdom.exception.BuildingDoesNotExistException;
import com.soze.idlekluch.kingdom.exception.UserDoesNotHaveKingdomException;
import com.soze.idlekluch.kingdom.repository.BuildingRepository;
import com.soze.idlekluch.user.entity.User;
import com.soze.idlekluch.user.exception.AuthUserDoesNotExistException;
import com.soze.idlekluch.user.service.UserService;
import com.soze.idlekluch.utils.io.FileUtils;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.idlekluch.world.repository.WorldRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BuildingServiceImpl implements BuildingService {

  private static final Logger LOG = LoggerFactory.getLogger(BuildingServiceImpl.class);

  private final UserService userService;
  private final KingdomService kingdomService;

  private final BuildingRepository buildingRepository;
  private final WorldRepository worldRepository;

  //TODO move to repository?
  private final Map<String, BuildingDefinitionDto> buildingDefinitions = new HashMap<>();

  @Value("buildings.json")
  private ClassPathResource buildings;

  @Autowired
  public BuildingServiceImpl(final UserService userService, final KingdomService kingdomService, final BuildingRepository buildingRepository, final WorldRepository worldRepository) {
    this.userService = Objects.requireNonNull(userService);
    this.kingdomService = Objects.requireNonNull(kingdomService);
    this.buildingRepository = Objects.requireNonNull(buildingRepository);
    this.worldRepository = Objects.requireNonNull(worldRepository);
  }

  @PostConstruct
  public void setup() throws IOException {
    LOG.info("Initializing building service.");

    //read buildings here
    final String fileContent = readBuildings();

    final ObjectMapper mapper = new ObjectMapper();
    mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

    final Map<String, Object> rawBuildingDefinitions = mapper.readValue(fileContent, new TypeReference<Map<String, Object>>() {
    });
    parseRawBuildingDefinitions(rawBuildingDefinitions);

    LOG.info("Read [{}] building definitions", buildingDefinitions.size());
  }

  private String readBuildings() {
    return FileUtils.readClassPathResource(buildings);
  }

  @Override
  public Map<String, BuildingDefinitionDto> getAllConstructableBuildings() {
    return buildingDefinitions;
  }

  @Override
  public Building buildBuilding(final String owner, final BuildBuildingForm form) {
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

    //TODO all that in the GameEngine
    //check world bounds
    //check for collisions with other buildings
    //check if player's kingdom has enough cash

    final Building building = constructBuilding(form);
    building.setKingdom(kingdom.get());

    buildingRepository.addBuilding(building);

    return building;
  }

  @Override
  public List<Building> getOwnBuildings(final String owner) {
    Objects.requireNonNull(owner);

    final Optional<Kingdom> kingdom = kingdomService.getUsersKingdom(owner);
    if (!kingdom.isPresent()) {
      LOG.info("User [{}] does not have a kingdom", owner);
      throw new UserDoesNotHaveKingdomException(owner);
    }

    return buildingRepository.getKingdomsBuildings(kingdom.get().getKingdomId());
  }

  @Override
  public List<Building> getAllConstructedBuildings() {
    return buildingRepository.getAllBuildings();
  }

  @Override
  public void destroyBuilding(final Building building) {
    Objects.requireNonNull(building);
    destroyBuilding(building.getBuildingId());
  }

  @Override
  public void destroyBuilding(final EntityUUID buildingId) {
    Objects.requireNonNull(buildingId);
    buildingRepository.removeBuilding(buildingId);
  }

  private void parseRawBuildingDefinitions(final Map<String, Object> data) {
    for (final Map.Entry<String, Object> entry : data.entrySet()) {
      final Map<String, Object> properties = (Map<String, Object>) entry.getValue();
      final BuildingType type = BuildingType.valueOf(((String) properties.get("type")).toUpperCase());

      switch (type) {
        case WAREHOUSE:
          parseWarehouseDefinition(properties);
          break;
        case GATHERER:
          parseGathererDefinition(properties);
          break;
      }

    }
  }

  private void parseWarehouseDefinition(final Map<String, Object> properties) {

    final WarehouseDefinitionDto warehouseDefinitionDto = new WarehouseDefinitionDto(
      (String) properties.get("id"),
      (String) properties.get("name"),
      BuildingType.WAREHOUSE,
      (int) properties.get("width"),
      (int) properties.get("height"),
      (String) properties.get("asset"),
      (int) properties.get("capacity")
    );

    buildingDefinitions.put((String) properties.get("id"), warehouseDefinitionDto);
  }

  private void parseGathererDefinition(final Map<String, Object> properties) {
      //TODO implement this
  }

  private Building constructBuilding(final BuildBuildingForm form) {
    Objects.requireNonNull(form);

    final BuildingDefinitionDto buildingDefinition = buildingDefinitions.get(form.getBuildingId());
    if (buildingDefinition == null) {
      throw new BuildingDoesNotExistException(form.getBuildingId());
    }

    final Building building = constructBuilding(buildingDefinition);

    building.setCreatedAt(Timestamp.from(Instant.now()));
    building.setName(buildingDefinition.getName());
    building.setX(form.getX());
    building.setY(form.getY());

    if (building == null) {
      throw new IllegalStateException("Building cannot be null");
    }

    return building;
  }

  private Building constructBuilding(final BuildingDefinitionDto buildingDefinition) {
    Objects.requireNonNull(buildingDefinition);

    switch (buildingDefinition.getType()) {
      case WAREHOUSE:
        return constructWarehouse((WarehouseDefinitionDto) buildingDefinition);
    }

    throw new IllegalStateException("Ops, could not construct building " + buildingDefinition.getId());
  }

  private Warehouse constructWarehouse(final WarehouseDefinitionDto warehouseDefinition) {
    Objects.requireNonNull(warehouseDefinition);

    final Warehouse warehouse = new Warehouse();
    warehouse.setBuildingId(EntityUUID.randomId());
    warehouse.setDefinitionId(warehouseDefinition.getId());

    //TODO resources are very unlikely to change during runtime, so they should be cached
    final List<Resource> allResources = worldRepository.getAllAvailableResources();
    final List<StorageUnit> storageUnits = allResources.stream().map(resource -> {
      final StorageUnit unit = new StorageUnit();
      unit.setAmount(0);
      unit.setCapacity(warehouseDefinition.getCapacity());
      unit.setResourceId(resource.getResourceId());
      return unit;
    })
      .collect(Collectors.toList());

    warehouse.setStorageUnits(storageUnits);

    return warehouse;
  }

}
