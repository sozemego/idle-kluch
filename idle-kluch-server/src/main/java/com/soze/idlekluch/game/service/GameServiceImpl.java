package com.soze.idlekluch.game.service;

import com.soze.idlekluch.game.engine.EntityConverter;
import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.game.message.EntityMessage;
import com.soze.idlekluch.game.message.WorldChunkMessage;
import com.soze.idlekluch.kingdom.service.BuildingService;
import com.soze.idlekluch.routes.Routes;
import com.soze.idlekluch.utils.JsonUtils;
import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.service.WorldService;
import com.soze.klecs.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

  private static final Logger LOG = LoggerFactory.getLogger(GameServiceImpl.class);

  private final WorldService worldService;
  private final WebSocketMessagingService webSocketMessagingService;
  private final BuildingService buildingService;
  private final GameEngine gameEngine;
  private final EntityConverter entityConverter;

  @Autowired
  public GameServiceImpl(final WorldService worldService,
                         final WebSocketMessagingService webSocketMessagingService,
                         final BuildingService buildingService,
                         final GameEngine gameEngine,
                         final EntityConverter entityConverter) {
    this.worldService = Objects.requireNonNull(worldService);
    this.webSocketMessagingService = Objects.requireNonNull(webSocketMessagingService);
    this.buildingService = Objects.requireNonNull(buildingService);
    this.gameEngine = Objects.requireNonNull(gameEngine);
    this.entityConverter = Objects.requireNonNull(entityConverter);
  }

  /**
   * Initializes the game.
   * Loads all entities from DB, converts them to game entities and adds them to the game engine.
   */
  @PostConstruct
  public void init() {
    LOG.info("Initializing the game");

    LOG.info("Initializing buildings");
    final List<PersistentEntity> buildings = buildingService.getAllConstructedBuildings();

    final List<Entity> buildingEntities = buildings
                                            .stream()
                                            .map(entityConverter::convertPersistentToEntity)
                                            .collect(Collectors.toList());

    buildingEntities.forEach(gameEngine::addEntity);
    LOG.info("Added [{}] building entities to engine", buildingEntities.size());

//    final List<Forest> allForests = worldRepository.getAllForests();
//    LOG.info("Retrieved [{}] forests", allForests.size());
//
//    if(allForests.isEmpty()) {
//      final int forests = 125;
//      LOG.info("There are no forests, generating [{]] forests!", forests);
//      final int maxX = worldWidth * tileSize;
//      final int maxY = worldHeight * tileSize;
//
//      for(int i = 0; i < forests; i++) {
//        final int x = CommonUtils.randomNumber(0, maxX);
//        final int y = CommonUtils.randomNumber(0, maxY);
//
//        final Forest forest = new Forest();
//        forest.setForestId(EntityUUID.randomId());
//        forest.setX(x);
//        forest.setY(y);
//        forest.setYield(25);
//        forest.setDefinitionId("forest_" + CommonUtils.randomNumber(1, 4));
//
//        worldRepository.addForest(forest);
//      }
//    }

//    final List<Forest> forests = world.getAllForests();
//    final List<Entity> treeEntities = forests.stream().map(entityConverter::convert).collect(Collectors.toList());
//    treeEntities.forEach(gameEngine::addEntity);
//    LOG.info("Added [{}] tree entities to engine", treeEntities.size());

  }

  /**
   * Responsibilities right now:
   * 1. Send all tile data to the joining player
   * 2. Send all entities to the joining player
   */
  @Override
  public void handleInitMessage(final String username) {
    LOG.info("Init message from [{}]", username);

    final Map<Point, Tile> allTiles = worldService.getAllTiles();

    final WorldChunkMessage worldChunkMessage = new WorldChunkMessage(new ArrayList<>(allTiles.values()));
    final String worldChunkJson = JsonUtils.objectToJson(worldChunkMessage);

    webSocketMessagingService.sendToUser(username, Routes.GAME + Routes.GAME_OUTBOUND, worldChunkJson);

    final List<Entity> entities = gameEngine.getAllEntities();
    final List<EntityMessage> entityMessages = entities.stream()
      .map(entityConverter::toMessage)
      .collect(Collectors.toList());

    entityMessages.forEach(message -> {
      final String entityJson = JsonUtils.objectToJson(message);
      webSocketMessagingService.sendToUser(username, Routes.GAME + Routes.GAME_OUTBOUND, entityJson);
    });
  }

  @Override
  public void handleBuildBuildingMessage(final String username, final BuildBuildingForm form) {
    final Entity building = buildingService.buildBuilding(username, form);

    final EntityMessage entityMessage = entityConverter.toMessage(building);
    final String entityMessageJSon = JsonUtils.objectToJson(entityMessage);

    webSocketMessagingService.send(Routes.GAME + Routes.GAME_OUTBOUND, entityMessageJSon);
  }

}
