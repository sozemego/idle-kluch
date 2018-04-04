package com.soze.idlekluch.game.service;

import com.soze.idlekluch.game.message.ConstructedBuildingMessage;
import com.soze.idlekluch.game.message.WorldChunkMessage;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class GameServiceImpl implements GameService {

  private static final Logger LOG = LoggerFactory.getLogger(GameServiceImpl.class);

  private final World world;
  private final WebSocketMessagingService webSocketMessagingService;
  private final BuildingService buildingService;
  private final BuildingDtoConverter buildingDtoConverter;

  @Autowired
  public GameServiceImpl(final World world,
                         final WebSocketMessagingService webSocketMessagingService,
                         final BuildingService buildingService,
                         final BuildingDtoConverter buildingDtoConverter) {
    this.world = Objects.requireNonNull(world);
    this.webSocketMessagingService = Objects.requireNonNull(webSocketMessagingService);
    this.buildingService = Objects.requireNonNull(buildingService);
    this.buildingDtoConverter = Objects.requireNonNull(buildingDtoConverter);
  }

  /**
   * Responsibilities right now:
   * 1. Send all tile data to the joining player
   * 2. Send all constructed building information to the player.
   * @param username
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

}
