package com.soze.idlekluch.world.service;

import com.soze.idlekluch.world.dto.ForestDefinitionDto;
import com.soze.idlekluch.world.events.InitializeWorldEvent;
import com.soze.idlekluch.world.repository.WorldRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

  @EventListener
  public void handleInitializeWorldEvent(final InitializeWorldEvent initializeWorldEvent) {
    LOG.info("[{}] received [{}]", this.getClass(), initializeWorldEvent);
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
