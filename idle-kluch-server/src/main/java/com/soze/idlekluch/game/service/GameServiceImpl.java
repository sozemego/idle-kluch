package com.soze.idlekluch.game.service;

import com.soze.idlekluch.game.message.WorldChunkMessage;
import com.soze.idlekluch.utils.JsonUtils;
import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.service.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameServiceImpl implements GameService {

  private static final Logger LOG = LoggerFactory.getLogger(GameServiceImpl.class);

//  @Autowired
  private final World world;

  @Autowired
  private SimpMessageSendingOperations messageTemplate;

//  private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

  private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();
  private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();


  @Autowired
  public GameServiceImpl(final World world) {
    this.world = Objects.requireNonNull(world);
  }

  @Override
  public void onConnect(final String sessionId, final String username) {
    LOG.info("Connected [{}][{}]", sessionId, username);
    sessionUserMap.put(sessionId, username);
    userSessionMap.put(username, sessionId);

    final Map<Point, Tile> allTiles = world.getAllTiles();
    final WorldChunkMessage worldChunkMessage = new WorldChunkMessage(new ArrayList<>(allTiles.values()));
    final String json = JsonUtils.objectToJson(worldChunkMessage);
    //SEND THIS DATA IN RESPONSE TO REQUEST
  }

  //TODO refactor to own class
  private MessageHeaders createHeaders(String sessionId) {
    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
    headerAccessor.setSessionId(sessionId);
    headerAccessor.setLeaveMutable(true);
    return headerAccessor.getMessageHeaders();
  }

  @Override
  public void onDisconnect(final String sessionId) {
    LOG.info("Disconnected [{}]", sessionId);
  }
}
