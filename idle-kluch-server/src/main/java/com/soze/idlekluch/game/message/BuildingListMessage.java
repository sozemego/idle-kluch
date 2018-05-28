package com.soze.idlekluch.game.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BuildingListMessage extends OutgoingMessage {

  private final List<EntityMessage> buildingDefinitions;

  @JsonCreator
  public BuildingListMessage(@JsonProperty("buildingDefinitions") final List<EntityMessage> buildingDefinitions) {
    super(OutgoingMessageType.BUILDING_LIST);
    this.buildingDefinitions = buildingDefinitions;
  }

  public List<EntityMessage> getBuildingDefinitions() {
    return buildingDefinitions;
  }

  @Override
  public String toString() {
    return "BuildingListMessage{" +
             "buildingDefinitions=" + buildingDefinitions +
             '}';
  }
}
