package com.soze.idlekluch.game.service;

import com.soze.idlekluch.aop.annotations.Profiled;
import com.soze.idlekluch.game.event.GameUpdatedEvent;
import com.soze.idlekluch.game.message.BuildBuildingForm;
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

  @EventListener
  void handleWorldChunkCreatedEvent(WorldChunkCreatedEvent event);

  @EventListener
  void handleRemovedEntityEvent(RemovedEntityEvent removedEntityEvent);

  @EventListener
  void handleGameUpdatedEvent(GameUpdatedEvent gameUpdatedEvent);

}
