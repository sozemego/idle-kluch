package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.kingdom.dto.BuildBuildingForm;
import com.soze.idlekluch.kingdom.dto.BuildingDefinitionDto;
import com.soze.idlekluch.kingdom.dto.BuildingDto;

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
  public void buildBuilding(final String owner, final BuildBuildingForm form);


  public List<BuildingDto> getOwnBuildings(final String owner);


}
