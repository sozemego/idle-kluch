package com.soze.idlekluch.game.service;

import org.springframework.web.socket.WebSocketSession;

public interface GameService {

  /**
   * Called when a new player joins the game.
   * @param session
   */
  void onConnect(final WebSocketSession session);

  /**
   * Called when a player disconnects.
   * @param session
   */
  void onDisconnect(final WebSocketSession session);

}
