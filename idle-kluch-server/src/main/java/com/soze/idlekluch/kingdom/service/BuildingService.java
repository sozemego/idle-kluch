package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.kingdom.dto.BuildingDefinitionDto;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.klecs.entity.Entity;

import java.util.List;
import java.util.Map;

public interface BuildingService {

  /**
   * Returns a list of constructable buildings.
   * This means all buildings, not specific to any kingdom or player.
   */
  Map<String, BuildingDefinitionDto> getAllConstructableBuildings();

  /**
   * Attempts to place a building for a given player.
   */
  Entity<EntityUUID> buildBuilding(final String owner, final BuildBuildingForm form);

  List<Entity<EntityUUID>> getOwnBuildings(final String owner);

  /**
   * Returns all buildings constructed in the game, from the DB.
   */
  List<PersistentEntity> getAllConstructedBuildings();

  /**
   * Attempts to delete a building with given buildingId.
   */
  void destroyBuilding(final EntityUUID buildingId);

}
