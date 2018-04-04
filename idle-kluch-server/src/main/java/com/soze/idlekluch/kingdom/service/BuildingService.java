package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.kingdom.dto.BuildBuildingForm;
import com.soze.idlekluch.kingdom.dto.BuildingDefinitionDto;
import com.soze.idlekluch.kingdom.entity.Building;

import java.util.List;

public interface BuildingService {

  /**
   * Returns a list of constructable buildings.
   * This means all buildings, not specific to any kingdom or player.
   *
   * This is a list of <code>Map<String, Object></code> objects,
   * because there isn't a class which represents building definitions.
   * @return
   */
  public List<BuildingDefinitionDto> getAllConstructableBuildings();

  /**
   * Attempts to place a building for a given player.
   * @param owner
   * @param form
   */
  public Building buildBuilding(final String owner, final BuildBuildingForm form);

  public List<Building> getOwnBuildings(final String owner);

  /**
   * Returns all buildings constructed in the game.
   * @return
   */
  public List<Building> getAllConstructedBuildings();

}
