package com.soze.idlekluch.game.service;

import com.soze.idlekluch.core.aop.annotations.AuthLog;
import com.soze.idlekluch.core.aop.annotations.Profiled;
import com.soze.idlekluch.game.engine.EntityConverter;
import com.soze.idlekluch.game.message.*;
import com.soze.idlekluch.kingdom.service.BuildingService;
import com.soze.idlekluch.core.routes.Routes;
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
   * 3. Send a list of all constructable buildings to the joining player
   */
  @Override
  @Profiled
  @AuthLog
  public void handleInitMessage(final String username) {

    final Map<TileId, Tile> allTiles = worldService.getAllTiles();
    final WorldChunkMessage worldChunkMessage = new WorldChunkMessage(new ArrayList<>(allTiles.values()));
    webSocketMessagingService.sendToUser(username, Routes.GAME_OUTBOUND, worldChunkMessage);

    gameEngine
      .getAllEntities()
      .stream()
      .map(entityConverter::toMessage)
      .forEach(message -> webSocketMessagingService.sendToUser(username, Routes.GAME_OUTBOUND, message));

    final List<Entity> buildingDefinitions = buildingService.getAllConstructableBuildings();
    final List<EntityMessage> convertedBuildingDefinitions = buildingDefinitions
                                                               .stream()
                                                               .map(entityConverter::toMessage)
                                                               .collect(Collectors.toList());
    webSocketMessagingService.sendToUser(username, Routes.GAME_OUTBOUND, new BuildingListMessage(convertedBuildingDefinitions));
  }

  @Override
  @Profiled
  @AuthLog
  public void handleBuildBuildingMessage(final String username, final BuildBuildingForm form) {
    final Entity building = buildingService.buildBuilding(username, form);

    final Optional<TileId> tileId = WorldUtils.getEntityTileId(building);
    worldService.createWorldChunk(tileId.get(), 5, 5);
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
  @EventListener
  public void handleWorldChunkCreatedEvent(final WorldChunkCreatedEvent event) {
    final List<Tile> tiles = event.getTiles();
    if(tiles.isEmpty()) {
      LOG.info("World chunk created without any tiles, ignoring.");
      return;
    }

    LOG.info("World chunk created, sending data to players!");
    webSocketMessagingService.send(Routes.GAME_OUTBOUND, new WorldChunkMessage(tiles));
  }

  @Override
  @EventListener
  @Profiled
  public void handleRemovedEntityEvent(final RemovedEntityEvent removedEntityEvent) {
    final RemoveEntityMessage removeEntityMessage = new RemoveEntityMessage(removedEntityEvent.getEntity().getId().toString());
    webSocketMessagingService.send(Routes.GAME_OUTBOUND, removeEntityMessage);
  }

  @Override
  @Profiled
  public void handlePauseToggle() {
    if(gameEngine.isPaused()) {
      LOG.info("Game was paused, starting!");
      gameEngine.start();
    } else {
      LOG.info("Game was running, pausing!");
      gameEngine.stop();
    }

    webSocketMessagingService.send(Routes.GAME_OUTBOUND, new PauseStateMessage(!gameEngine.isPaused()));
  }

}
