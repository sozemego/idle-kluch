package com.soze.idlekluch.game.service;

import com.soze.idlekluch.game.message.AttachResourceSourceForm;
import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.game.message.UpgradeComponentMessage;
import com.soze.idlekluch.world.events.WorldChunkCreatedEvent;
import com.soze.klecs.engine.RemovedEntityEvent;

public interface GameService {

  /**
   * Called when a new player joins the game.
   */
  void handleInitMessage(String username);

  /**
   * Attempts to place a building for a given player.
   */
  void handleBuildBuildingMessage(String owner, BuildBuildingForm form);

  void handleAttachResourceMessage(String owner, AttachResourceSourceForm form);

  void handleUpgradeComponentMessage(String owner, UpgradeComponentMessage upgradeComponentMessage);

  void handleDuplicateSession(String sessionId);

  void handleWorldChunkCreatedEvent(WorldChunkCreatedEvent event);

  void handleRemovedEntityEvent(RemovedEntityEvent removedEntityEvent);

  void handlePauseToggle();

}
