package com.soze.idlekluch.kingdom.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soze.idlekluch.kingdom.dto.BuildingDefinitionDto;
import com.soze.idlekluch.kingdom.dto.BuildingDefinitionDto.BuildingType;
import com.soze.idlekluch.kingdom.dto.WarehouseDefinitionDto;
import com.soze.idlekluch.utils.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BuildingServiceImpl implements BuildingService {

  private static final Logger LOG = LoggerFactory.getLogger(BuildingServiceImpl.class);

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

    final Map<String, Object> rawBuildingDefinitions = mapper.readValue(fileContent, new TypeReference<Map<String, Object>>(){});
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

  private void parseRawBuildingDefinitions(final Map<String, Object> data) {
    for(final Map.Entry<String, Object> entry: data.entrySet()) {
      final Map<String, Object> properties = (Map<String, Object>) entry.getValue();
      final BuildingType type = BuildingType.valueOf(((String)properties.get("type")).toUpperCase());

      switch (type) {
        case WAREHOUSE: parseWarehouseDefinition(properties);
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
