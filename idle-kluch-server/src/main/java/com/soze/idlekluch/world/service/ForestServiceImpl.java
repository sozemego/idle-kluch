package com.soze.idlekluch.world.service;

import com.soze.idlekluch.world.dto.ForestDefinitionDto;
import com.soze.idlekluch.world.entity.Forest;
import com.soze.idlekluch.world.repository.WorldRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.soze.idlekluch.utils.JsonUtils.resourceToMap;

@Service
public class ForestServiceImpl implements ForestService {

  private static final Logger LOG = LoggerFactory.getLogger(ForestServiceImpl.class);

  private final WorldRepository worldRepository;

  private final Map<String, ForestDefinitionDto> forestDefinitions = new HashMap<>();

  @Value("forests.json")
  private ClassPathResource forests;

  @Autowired
  public ForestServiceImpl(final WorldRepository worldRepository) {
    this.worldRepository = Objects.requireNonNull(worldRepository);
  }

  @PostConstruct
  public void setup() throws IOException {
    LOG.info("Initializing ForestService");

    //read trees
    final Map<String, Object> rawTreeDefinitions = resourceToMap(forests, String.class, Object.class);
    parseRawForestDefinitions(rawTreeDefinitions);
    LOG.info("Loaded [{}] forest definitions", forestDefinitions.size());
  }

  @Override
  public List<Forest> getAllTrees() {
    return worldRepository.getAllForests();
  }

  @Override
  public void addForest(final Forest forest) {
    worldRepository.addForest(forest);
  }

  @Override
  public String getForestAsset(final String definitionId) {
    return forestDefinitions.get(definitionId).getAsset();
  }

  private void parseRawForestDefinitions(final Map<String, Object> data) {
    for(final Map.Entry<String, Object> entry: data.entrySet()) {
      final Map<String, Object> properties = (Map<String, Object>) entry.getValue();

      final ForestDefinitionDto forestDefinitionDto = new ForestDefinitionDto(
        (String) properties.get("id"),
        (String) properties.get("asset"),
        (int) properties.get("width"),
        (int) properties.get("height")
      );

      forestDefinitions.put(forestDefinitionDto.getId(), forestDefinitionDto);
    }
  }

}
