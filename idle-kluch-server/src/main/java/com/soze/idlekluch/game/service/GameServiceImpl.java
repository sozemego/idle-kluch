package com.soze.idlekluch.game.service;

import com.soze.idlekluch.world.service.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;

@Service
public class GameServiceImpl implements GameService {

  private static final Logger LOG = LoggerFactory.getLogger(GameServiceImpl.class);

  private final World world;

  @Autowired
  public GameServiceImpl(World world) {
    this.world = Objects.requireNonNull(world);
  }

  @Override
  public void onConnect(WebSocketSession session) {
    LOG.info("Connected [{}]", session.getId());
  }

  @Override
  public void onDisconnect(WebSocketSession session) {
    LOG.info("Disconnected [{}]", session.getId());
  }

}
