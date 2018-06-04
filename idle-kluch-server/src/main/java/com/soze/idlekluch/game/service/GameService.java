package com.soze.idlekluch.game.service;

import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.game.message.PauseToggleMessage;
import com.soze.idlekluch.world.events.WorldChunkCreatedEvent;
import com.soze.klecs.engine.RemovedEntityEvent;
import org.springframework.context.event.EventListener;

public interface GameService {

  /**
   * Called when a new player joins the game.
   */
  void handleInitMessage(String username);

  /**
   * Attempts to place a building for a given player.
   */
  void handleBuildBuildingMessage(String owner, BuildBuildingForm form);

  void handleDuplicateSession(String sessionId);

  void handleWorldChunkCreatedEvent(WorldChunkCreatedEvent event);

  void handleRemovedEntityEvent(RemovedEntityEvent removedEntityEvent);

  void handlePauseToggle();

}
