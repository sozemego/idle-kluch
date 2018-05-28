package com.soze.idlekluch.game.service;

import com.soze.idlekluch.aop.annotations.Authorized;
import com.soze.idlekluch.aop.annotations.Profiled;
import com.soze.idlekluch.game.engine.EntityConverter;
import com.soze.idlekluch.game.message.*;
import com.soze.idlekluch.kingdom.service.BuildingService;
import com.soze.idlekluch.routes.Routes;
import com.soze.idlekluch.utils.JsonUtils;
import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.entity.TileId;
import com.soze.idlekluch.world.events.WorldChunkCreatedEvent;
import com.soze.idlekluch.world.service.WorldService;
import com.soze.idlekluch.world.utils.WorldUtils;
import com.soze.klecs.engine.RemovedEntityEvent;
import com.soze.klecs.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.soze.idlekluch.world.utils.WorldUtils.getEntityTileId;

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
  @Profiled
  @Authorized
  public void handleInitMessage(final String username) {

    final Map<TileId, Tile> allTiles = worldService.getAllTiles();
    final WorldChunkMessage worldChunkMessage = new WorldChunkMessage(new ArrayList<>(allTiles.values()));
    final String worldChunkJson = JsonUtils.objectToJson(worldChunkMessage);

    webSocketMessagingService.sendToUser(username, Routes.GAME_OUTBOUND, worldChunkJson);

    final List<Entity> entities = gameEngine.getAllEntities();
    final List<EntityMessage> entityMessages = entities.stream()
                                                 .map(entityConverter::toMessage)
                                                 .collect(Collectors.toList());

    entityMessages.forEach(message -> {
      final String entityJson = JsonUtils.objectToJson(message);
      webSocketMessagingService.sendToUser(username, Routes.GAME_OUTBOUND, entityJson);
    });

    final List<Entity> buildingDefinitions = buildingService.getAllConstructableBuildings();
    final List<EntityMessage> convertedBuildingDefinitions = buildingDefinitions
                                           .stream()
                                           .map(entityConverter::toMessage)
                                           .collect(Collectors.toList());

    webSocketMessagingService.sendToUser(username, Routes.GAME_OUTBOUND, new BuildingListMessage(convertedBuildingDefinitions));
  }

  @Override
  @Profiled
  @Authorized
  public void handleBuildBuildingMessage(final String username, final BuildBuildingForm form) {
    final Entity building = buildingService.buildBuilding(username, form);

    final EntityMessage entityMessage = entityConverter.toMessage(building);
    final String entityMessageJSon = JsonUtils.objectToJson(entityMessage);

    final Optional<TileId> tileId = WorldUtils.getEntityTileId(building);

    worldService.createWorldChunk(tileId.get(), 5, 5);

    webSocketMessagingService.send(Routes.GAME_OUTBOUND, entityMessageJSon);
  }

  @Override
  @Profiled
  public void handleDuplicateSession(final String sessionId) {
    Objects.requireNonNull(sessionId);
    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
                                                 .create(SimpMessageType.MESSAGE);
    headerAccessor.setSessionId(sessionId);
    headerAccessor.setLeaveMutable(true);

    webSocketMessagingService.sendToSession(
      sessionId,
      Routes.GAME_OUTBOUND,
      new AlreadyConnectedMessage(),
      headerAccessor.getMessageHeaders()
    );
  }

  @Override
  @Profiled
  public void handleWorldChunkCreatedEvent(final WorldChunkCreatedEvent event) {
    Objects.requireNonNull(event);

    final List<Tile> tiles = event.getTiles();
    if(tiles.isEmpty()) {
      LOG.info("World chunk created without any tiles, ignoring.");
      return;
    }

    LOG.info("World chunk created, sending data to players!");
    final WorldChunkMessage worldChunkMessage = new WorldChunkMessage(event.getTiles());
    webSocketMessagingService.send(Routes.GAME_OUTBOUND, worldChunkMessage);
  }

  @Override
  public void handleRemovedEntityEvent(final RemovedEntityEvent removedEntityEvent) {
    Objects.requireNonNull(removedEntityEvent);

    final RemoveEntityMessage removeEntityMessage = new RemoveEntityMessage(removedEntityEvent.getEntity().getId().toString());
    webSocketMessagingService.send(Routes.GAME_OUTBOUND, removeEntityMessage);
  }

}
