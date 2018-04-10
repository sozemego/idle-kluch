package com.soze.idlekluch.game.service;

import com.soze.idlekluch.game.message.ConstructedBuildingMessage;
import com.soze.idlekluch.game.message.WorldChunkMessage;
import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.kingdom.dto.BuildingDto;
import com.soze.idlekluch.kingdom.service.BuildingDtoConverter;
import com.soze.idlekluch.kingdom.entity.Building;
import com.soze.idlekluch.kingdom.service.BuildingService;
import com.soze.idlekluch.routes.Routes;
import com.soze.idlekluch.utils.JsonUtils;
import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.service.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.*;
import java.util.List;

@Service
public class GameServiceImpl implements GameService {

  private static final Logger LOG = LoggerFactory.getLogger(GameServiceImpl.class);

  private final World world;
  private final WebSocketMessagingService webSocketMessagingService;
  private final BuildingService buildingService;
  private final BuildingDtoConverter buildingDtoConverter;
  private final GameEngine gameEngine;

  @Autowired
  public GameServiceImpl(final World world,
                         final WebSocketMessagingService webSocketMessagingService,
                         final BuildingService buildingService,
                         final BuildingDtoConverter buildingDtoConverter,
                         final GameEngine gameEngine) {
    this.world = Objects.requireNonNull(world);
    this.webSocketMessagingService = Objects.requireNonNull(webSocketMessagingService);
    this.buildingService = Objects.requireNonNull(buildingService);
    this.buildingDtoConverter = Objects.requireNonNull(buildingDtoConverter);
    this.gameEngine = Objects.requireNonNull(gameEngine);
  }

  /**
   * Initializes the game.
   * Loads all entities from DB, converts them to game entities and adds them to the game engine.
   */
  @PostConstruct
  public void init() {
    LOG.info("Initializing the game");

    LOG.info("Initializing buildings");
    final List<Building> buildings = buildingService.getAllConstructedBuildings();


  }

  /**
   * Responsibilities right now:
   * 1. Send all tile data to the joining player
   * 2. Send all constructed building information to the player.
   */
  @Override
  public void handleInitMessage(final String username) {
    LOG.info("Init message from [{}]", username);

    final Map<Point, Tile> allTiles = world.getAllTiles();

    final WorldChunkMessage worldChunkMessage = new WorldChunkMessage(new ArrayList<>(allTiles.values()));
    final String worldChunkJson = JsonUtils.objectToJson(worldChunkMessage);

    webSocketMessagingService.sendToUser(username, Routes.GAME + Routes.GAME_OUTBOUND, worldChunkJson);

    final List<Building> buildings = buildingService.getAllConstructedBuildings();
    final List<BuildingDto> buildingDtos = buildingDtoConverter.convertBuildings(buildings);
    final ConstructedBuildingMessage constructedBuildingMessage = new ConstructedBuildingMessage(buildingDtos);
    final String constructedBuildingsJson = JsonUtils.objectToJson(constructedBuildingMessage);

    webSocketMessagingService.sendToUser(username, Routes.GAME + Routes.GAME_OUTBOUND, constructedBuildingsJson);
  }

  @Override
  public void handleBuildBuildingMessage(final String username, final BuildBuildingForm form) {
    final Building building = buildingService.buildBuilding(username, form);

    final BuildingDto buildingDto = buildingDtoConverter.convertBuilding(building);
    final ConstructedBuildingMessage constructedBuildingMessage = new ConstructedBuildingMessage(Collections.singletonList(buildingDto));
    final String constructedBuildingsJson = JsonUtils.objectToJson(constructedBuildingMessage);

    webSocketMessagingService.send(Routes.GAME + Routes.GAME_OUTBOUND, constructedBuildingsJson);
  }

}
