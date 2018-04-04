package com.soze.idlekluch.game.service;

public interface GameService {

  /**
   * Called when a new player joins the game.
   */
  void handleInitMessage(final String username);

}
