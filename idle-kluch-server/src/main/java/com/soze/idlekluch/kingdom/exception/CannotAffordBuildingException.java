package com.soze.idlekluch.kingdom.exception;

import com.soze.idlekluch.game.exception.GameException;

import java.util.Objects;
import java.util.UUID;

public class CannotAffordBuildingException extends GameException {

  private final String buildingId;
  private final long playerBucks;
  private final long cost;

  public CannotAffordBuildingException(final UUID messageId,
                                       final String buildingId,
                                       final long playerBucks,
                                       final long cost) {
    super(messageId);
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
