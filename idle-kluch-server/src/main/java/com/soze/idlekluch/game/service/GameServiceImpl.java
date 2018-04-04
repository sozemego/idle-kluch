package com.soze.idlekluch.game.service;

import com.soze.idlekluch.game.message.WorldChunkMessage;
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
import java.util.Map;
import java.util.Objects;

@Service
public class GameServiceImpl implements GameService {

  private static final Logger LOG = LoggerFactory.getLogger(GameServiceImpl.class);

  private final World world;
  private final WebSocketMessagingService webSocketMessagingService;

  @Autowired
  public GameServiceImpl(final World world, WebSocketMessagingService webSocketMessagingService) {
    this.world = Objects.requireNonNull(world);
    this.webSocketMessagingService = Objects.requireNonNull(webSocketMessagingService);
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
    final String json = JsonUtils.objectToJson(worldChunkMessage);

    webSocketMessagingService.sendToUser(username, Routes.GAME + Routes.GAME_OUTBOUND, json);
  }

}
