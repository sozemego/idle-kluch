package com.soze.idlekluch.world.service;

import com.soze.idlekluch.world.dto.TreeDefinitionDto;
import com.soze.idlekluch.world.entity.Tree;
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
public class TreeServiceImpl implements TreeService {

  private static final Logger LOG = LoggerFactory.getLogger(TreeServiceImpl.class);

  private final WorldRepository worldRepository;

  private final Map<String, TreeDefinitionDto> treeDefinitions = new HashMap<>();

  @Value("trees.json")
  private ClassPathResource trees;

  @Autowired
  public TreeServiceImpl(final WorldRepository worldRepository) {
    this.worldRepository = Objects.requireNonNull(worldRepository);
  }

  @PostConstruct
  public void setup() throws IOException {
    LOG.info("Initializing TreeService");

    //read trees
    final Map<String, Object> rawTreeDefinitions = resourceToMap(trees, String.class, Object.class);
    parseRawTreeDefinitions(rawTreeDefinitions);
    LOG.info("Loaded [{}] tree definitions", treeDefinitions.size());
  }

  @Override
  public List<Tree> getAllTrees() {
    return worldRepository.getAllTrees();
  }

  @Override
  public void addTree(final Tree tree) {
    worldRepository.addTree(tree);
  }

  @Override
  public String getTreeAsset(final String definitionId) {
    return treeDefinitions.get(definitionId).getAsset();
  }

  private void parseRawTreeDefinitions(final Map<String, Object> data) {
    for(final Map.Entry<String, Object> entry: data.entrySet()) {
      final Map<String, Object> properties = (Map<String, Object>) entry.getValue();

      final TreeDefinitionDto treeDefinitionDto = new TreeDefinitionDto(
        (String) properties.get("id"),
        (String) properties.get("asset"),
        (int) properties.get("width"),
        (int) properties.get("height")
      );

      treeDefinitions.put(treeDefinitionDto.getId(), treeDefinitionDto);
    }
  }

}
