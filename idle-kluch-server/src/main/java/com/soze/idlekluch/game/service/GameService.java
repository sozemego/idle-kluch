package com.soze.idlekluch.game.service;

import com.soze.idlekluch.game.message.BuildBuildingForm;

public interface GameService {

  /**
   * Called when a new player joins the game.
   */
  void handleInitMessage(final String username);

  /**
   * Attempts to place a building for a given player.
   */
  void handleBuildBuildingMessage(final String owner, final BuildBuildingForm form);

}
