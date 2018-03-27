package com.soze.idlekluch.kingdom.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soze.idlekluch.kingdom.dto.BuildBuildingForm;
import com.soze.idlekluch.kingdom.dto.BuildingDefinitionDto;
import com.soze.idlekluch.kingdom.dto.BuildingDto;
import com.soze.idlekluch.kingdom.dto.BuildingDto.BuildingType;
import com.soze.idlekluch.kingdom.dto.WarehouseDefinitionDto;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.exception.UserDoesNotHaveKingdomException;
import com.soze.idlekluch.user.entity.User;
import com.soze.idlekluch.user.exception.AuthUserDoesNotExistException;
import com.soze.idlekluch.user.service.UserService;
import com.soze.idlekluch.utils.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@Service
public class BuildingServiceImpl implements BuildingService {

  private static final Logger LOG = LoggerFactory.getLogger(BuildingServiceImpl.class);

  private final UserService userService;
  private final KingdomService kingdomService;

  @Autowired
  public BuildingServiceImpl(final UserService userService, final KingdomService kingdomService) {
    this.userService = Objects.requireNonNull(userService);
    this.kingdomService = Objects.requireNonNull(kingdomService);
  }

  //TODO move to repository?
  private final Map<String, BuildingDefinitionDto> buildingDefinitions = new HashMap<>();

  @Value("buildings.json")
  private ClassPathResource buildings;

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
    System.out.println(buildingDefinitions);

    //then later create them, add to ECS, transform them etc.
  }

  private String readBuildings() {
    return FileUtils.readClassPathResource(buildings);
  }

  @Override
  public List<BuildingDefinitionDto> getAllConstructableBuildings() {
    return new ArrayList<>(buildingDefinitions.values());
  }

  @Override
  public void buildBuilding(final String owner, final BuildBuildingForm form) {
    Objects.requireNonNull(owner);
    Objects.requireNonNull(form);

    LOG.info("User [{}] is trying to place a building [{}]", owner, form);

    //check if owner exists, just in case
    final Optional<User> userOptional = userService.getUserByUsername(owner);
    if(!userOptional.isPresent()) {
      LOG.info("User [{}] does not exist, cannot place building [{}]", owner, form);
      throw new AuthUserDoesNotExistException(owner);
    }

    final User user = userOptional.get();

    //now get user's kingdom
    final Optional<Kingdom> kingdom = kingdomService.getUsersKingdom(owner);
    if(!kingdom.isPresent()) {
      LOG.info("User [{}] does not have a kingdom", owner);
      throw new UserDoesNotHaveKingdomException(owner);
    }

    throw new IllegalStateException("Cannot place buildings yet!");
  }

  @Override
  public List<BuildingDto> getOwnBuildings(final String owner) {
    Objects.requireNonNull(owner);

    return null;
  }

  private void parseRawBuildingDefinitions(final Map<String, Object> data) {
    for (final Map.Entry<String, Object> entry : data.entrySet()) {
      final Map<String, Object> properties = (Map<String, Object>) entry.getValue();
      final BuildingType type = BuildingType.valueOf(((String) properties.get("type")).toUpperCase());

      switch (type) {
        case WAREHOUSE:
          parseWarehouseDefinition(properties);
          break;
      }

    }
  }

  private void parseWarehouseDefinition(final Map<String, Object> properties) {

    final WarehouseDefinitionDto warehouseDefinitionDto = new WarehouseDefinitionDto(
      (String) properties.get("id"),
      (String) properties.get("name"),
      BuildingType.WAREHOUSE
    );

    buildingDefinitions.put((String) properties.get("id"), warehouseDefinitionDto);
  }

}
