package com.soze.idlekluch.game.service;


import com.soze.idlekluch.core.aop.annotations.AuthLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameConnectionRegistryServiceImpl implements GameConnectionRegistryService {

  private static final Logger LOG = LoggerFactory.getLogger(GameConnectionRegistryServiceImpl.class);

  private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();
  private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();
  private final Set<String> duplicateSessionIds = Collections.synchronizedSet(new HashSet<>());

  @Override
  @AuthLog
  public void onConnect(final String sessionId, final String username) {
    Objects.requireNonNull(sessionId);
    Objects.requireNonNull(username);

    if(userSessionMap.containsKey(username)) {
      LOG.info("[{}] already connected!", username);
      duplicateSessionIds.add(sessionId);
    } else {
      sessionUserMap.put(sessionId, username);
      userSessionMap.put(username, sessionId);
    }
  }

  @Override
  @AuthLog
  public void onDisconnect(final String sessionId) {
    Objects.requireNonNull(sessionId);

    if(duplicateSessionIds.contains(sessionId)) {
      LOG.info("[{}] sessionId was in duplicates, now is disconnected", sessionId);
      duplicateSessionIds.remove(sessionId);
    } else {
      final String username = sessionUserMap.remove(sessionId);
      if(username != null) {
        userSessionMap.remove(username);
      }
    }
  }

  @Override
  public boolean isDuplicate(final String sessionId) {
    Objects.requireNonNull(sessionId);
    return duplicateSessionIds.contains(sessionId);
  }


}
