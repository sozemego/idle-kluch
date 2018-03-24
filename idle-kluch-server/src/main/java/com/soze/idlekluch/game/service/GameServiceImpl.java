package com.soze.idlekluch.game.service;

import com.soze.idlekluch.game.message.WorldChunkMessage;
import com.soze.idlekluch.utils.JsonUtils;
import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.service.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

@Service
public class GameServiceImpl implements GameService {

  private static final Logger LOG = LoggerFactory.getLogger(GameServiceImpl.class);

  private final World world;

  @Autowired
  public GameServiceImpl(final World world) {
    this.world = Objects.requireNonNull(world);
  }

  @Override
  public void onConnect(final WebSocketSession session) {
    LOG.info("Connected [{}]", session.getId());

    final Map<Point, Tile> allTiles = world.getAllTiles();

    //convert tiles to a json
    final WorldChunkMessage worldChunkMessage = new WorldChunkMessage(new ArrayList<>(allTiles.values()));
    final String json = JsonUtils.objectToJson(worldChunkMessage);

    //send
    try {
      LOG.info("Sending tiles to [{}]", session.getId());
      session.sendMessage(new TextMessage(json));
      LOG.info("Sent [{}] tiles to [{}]", allTiles.size(), session.getId());
    } catch (IOException e) {
      LOG.error("Problem while sending world chunk message to [{}]", session.getId(), e);
    }

  }

  @Override
  public void onDisconnect(final WebSocketSession session) {
    LOG.info("Disconnected [{}]", session.getId());
  }

}
