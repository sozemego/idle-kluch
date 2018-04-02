package com.soze.idlekluch.game.service;

import com.soze.idlekluch.game.message.WorldChunkMessage;
import com.soze.idlekluch.routes.Routes;
import com.soze.idlekluch.utils.JsonUtils;
import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.service.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameServiceImpl implements GameService {

  private static final Logger LOG = LoggerFactory.getLogger(GameServiceImpl.class);

  @Autowired
  private World world;

  @Autowired
  private SimpMessageSendingOperations messageTemplate;

//  private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

  private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();
  private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();


//  @Autowired
//  public GameServiceImpl(final World world) {
//    this.world = Objects.requireNonNull(world);
//  }

  @Override
  public void onConnect(final WebSocketSession session) {
    LOG.info("Connected [{}]", session.getId());
//    sessions.put(session.getId(), session);

    final Map<Point, Tile> allTiles = world.getAllTiles();
//    System.out.println(SecurityContextHolder.getContext().getAuthentication());
//    System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    //convert tiles to a json
//    maybe send buildings here too?
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
//    sessions.remove(session.getId());
  }

  @Override
  public void onConnect(final String sessionId, final String username) {
    LOG.info("Connected [{}][{}]", sessionId, username);
    sessionUserMap.put(sessionId, username);
    userSessionMap.put(username, sessionId);

    System.out.println(SecurityContextHolder.getContext().getAuthentication());

    final Map<Point, Tile> allTiles = world.getAllTiles();
    final WorldChunkMessage worldChunkMessage = new WorldChunkMessage(new ArrayList<>(allTiles.values()));
    final String json = JsonUtils.objectToJson(worldChunkMessage);

    messageTemplate.convertAndSendToUser(sessionId, Routes.GAME, json);
  }

  @Override
  public void onDisconnect(final String sessionId) {
    LOG.info("Disconnected [{}]", sessionId);
  }
}
