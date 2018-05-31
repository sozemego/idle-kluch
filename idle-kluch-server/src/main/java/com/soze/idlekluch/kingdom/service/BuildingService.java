package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.klecs.engine.RemovedEntityEvent;
import com.soze.klecs.entity.Entity;
import org.springframework.context.event.EventListener;

import java.util.List;

public interface BuildingService {

  /**
   * Attempts to place a building for a given player.
   */
  Entity buildBuilding(String owner, BuildBuildingForm form);

  List<Entity> getOwnBuildings(String owner);

  List<Entity> getAllConstructableBuildings();

  /**
   * Returns all buildings constructed in the game, from the DB.
   */
  List<PersistentEntity> getAllConstructedBuildings();

  /**
   * Attempts to delete a building with given buildingId.
   */
  void destroyBuilding(EntityUUID buildingId);

  @EventListener
  void handleRemovedEntity(RemovedEntityEvent removedEntityEvent);

}
