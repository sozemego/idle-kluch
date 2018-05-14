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

  /**
   * If player with the same username logs in twice or more times, his subsequent sessionIds
   * will be marked as duplicate. This method returns true if given sessionId is a duplicate.
   * false otherwise.
   * @return true if given sessionId was added to collection of duplicates, false otherwise
   */
  boolean isDuplicate(final String sessionId);

}
