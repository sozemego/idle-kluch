package com.soze.idlekluch.game.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.soze.idlekluch.game.message.IncomingMessage;

import java.util.Objects;

public class BuildBuildingForm extends IncomingMessage {

  private final String buildingId;
  private final int x;
  private final int y;

  @JsonCreator
  public BuildBuildingForm(@JsonProperty("buildingId") final String buildingId,
                           @JsonProperty("x") final int x,
                           @JsonProperty("y") final int y) {
    super(IncomingMessageType.BUILD_BUILDING);
    this.buildingId = Objects.requireNonNull(buildingId);
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
