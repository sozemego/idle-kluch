package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.kingdom.dto.BuildingDefinitionDto;
import com.soze.idlekluch.kingdom.entity.Building;

import java.util.List;
import java.util.Map;

public interface BuildingService {

  /**
   * Returns a list of constructable buildings.
   * This means all buildings, not specific to any kingdom or player.
   */
  public Map<String, BuildingDefinitionDto> getAllConstructableBuildings();

  /**
   * Attempts to place a building for a given player.
   */
  public Building buildBuilding(final String owner, final BuildBuildingForm form);

  public List<Building> getOwnBuildings(final String owner);

  /**
   * Returns all buildings constructed in the game.
   */
  public List<Building> getAllConstructedBuildings();

}
