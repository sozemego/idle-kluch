package com.soze.idlekluch.kingdom.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BuildBuildingForm {

  private final int buildingId;
  private final int x;
  private final int y;

  @JsonCreator
  public BuildBuildingForm(@JsonProperty("buildingId") final int buildingId,
                           @JsonProperty("x") final int x,
                           @JsonProperty("y") final int y) {
    this.buildingId = buildingId;
    this.x = x;
    this.y = y;
  }

  public int getBuildingId() {
    return buildingId;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }
}
