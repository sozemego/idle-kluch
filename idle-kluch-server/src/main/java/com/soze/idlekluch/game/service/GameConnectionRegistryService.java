package com.soze.idlekluch.game.service;

/**
 * A service whose purpose is to store and query WebSocket connection data.
 */
public interface GameConnectionRegistryService {

  /**
   * Called when a player connects through STOMP.
   */
  void onConnect(final String sessionId, final String username);

  /**
   * Called when a player disconnects (STOMP).
   */
  void onDisconnect(final String sessionId);

}
