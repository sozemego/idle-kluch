package com.soze.idlekluch.game.service;

public interface GameService {

  /**
   * Called when a player connects through STOMP.
   * @param sessionId
   * @param username
   */
  void onConnect(final String sessionId, final String username);

  /**
   * Called when a player disconnects (STOMP).
   * @param sessionId
   */
  void onDisconnect(final String sessionId);

}
