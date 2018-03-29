package com.soze.idlekluch.kingdom.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BuildBuildingForm {

  private final String buildingId;
  private final int x;
  private final int y;

  @JsonCreator
  public BuildBuildingForm(@JsonProperty("buildingId") final String buildingId,
                           @JsonProperty("x") final int x,
                           @JsonProperty("y") final int y) {
    this.buildingId = buildingId;
    this.x = x;
    this.y = y;
  }

  public String getBuildingId() {
    return buildingId;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  @Override
  public String toString() {
    return "BuildBuildingForm{" +
      "buildingId='" + buildingId + '\'' +
      ", x=" + x +
      ", y=" + y +
      '}';
  }
}
