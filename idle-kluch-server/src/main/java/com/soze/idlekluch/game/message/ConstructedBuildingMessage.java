package com.soze.idlekluch.game.message;

import com.soze.idlekluch.kingdom.dto.BuildingDto;

import java.util.List;
import java.util.Objects;

public class ConstructedBuildingMessage extends OutgoingMessage {

  private final List<BuildingDto> buildings;

  public ConstructedBuildingMessage(final List<BuildingDto> buildings) {
    super(OutgoingMessageType.CONSTRUCTED_BUILDING);
    this.buildings = Objects.requireNonNull(buildings);
  }

  public List<BuildingDto> getBuildings() {
    return buildings;
  }
}
