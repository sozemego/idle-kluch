package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.kingdom.dto.BuildingDefinitionDto;

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


}
