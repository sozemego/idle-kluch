package com.soze.idlekluch.kingdom.exception;

import java.util.Objects;

public class CannotAffordBuildingException extends RuntimeException {

  private final String buildingId;
  private final long playerBucks;
  private final long cost;

  public CannotAffordBuildingException(final String buildingId, final long playerBucks, final long cost) {
    this.buildingId = Objects.requireNonNull(buildingId);
    this.playerBucks = playerBucks;
    this.cost = cost;
  }

  public String getBuildingId() {
    return buildingId;
  }

  public long getPlayerBucks() {
    return playerBucks;
  }

  public long getCost() {
    return cost;
  }
}
