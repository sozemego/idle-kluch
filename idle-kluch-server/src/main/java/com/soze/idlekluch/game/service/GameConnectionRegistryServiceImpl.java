package com.soze.idlekluch.game.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameConnectionRegistryServiceImpl implements GameConnectionRegistryService {

  private static final Logger LOG = LoggerFactory.getLogger(GameConnectionRegistryServiceImpl.class);

  private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();
  private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();

  @Override
  public void onConnect(String sessionId, String username) {
    LOG.info("Session [{}], user [{}] connected", sessionId, username);
    sessionUserMap.put(sessionId, username);
    userSessionMap.put(username, sessionId);
  }

  @Override
  public void onDisconnect(String sessionId) {
    LOG.info("Session [{}] disconnected", sessionId);
    final String username = sessionUserMap.remove(sessionId);
    userSessionMap.remove(username);
  }
}
