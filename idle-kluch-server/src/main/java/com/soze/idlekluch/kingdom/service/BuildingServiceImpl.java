package com.soze.idlekluch.kingdom.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soze.idlekluch.utils.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class BuildingServiceImpl implements BuildingService {

  private static final Logger LOG = LoggerFactory.getLogger(BuildingServiceImpl.class);

  private final Map<String, Object> buildingDefinitions = new HashMap<>();

  @Value("buildings.json")
  private ClassPathResource buildings;

  @PostConstruct
  public void setup() throws IOException {
    LOG.info("Initializing building service.");

    //read buildings here
    final String fileContent = readBuildings();

    //read it as a List of Maps
    final ObjectMapper mapper = new ObjectMapper();
    mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    buildingDefinitions.putAll(mapper.readValue(fileContent, new TypeReference<Map<String, Object>>(){}));
    LOG.info("Read [{}] building definitions", buildingDefinitions.size());
    //TODO ? create building descriptions here

    //then later create them, add to ECS, transform them etc.
  }

  private String readBuildings() {
    return FileUtils.readClassPathResource(buildings);
  }

}
