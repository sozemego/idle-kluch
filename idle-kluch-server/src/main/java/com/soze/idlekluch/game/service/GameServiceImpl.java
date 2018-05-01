package com.soze.idlekluch.game.service;

import com.soze.idlekluch.game.engine.EntityConverter;
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
   * Responsibilities right now:
   * 1. Send all tile data to the joining player
   * 2. Send all entities to the joining player
   */
  @Override
  public void handleInitMessage(final String username) {
    LOG.info("[GAME_SERVICE] Init message from [{}]", username);

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
