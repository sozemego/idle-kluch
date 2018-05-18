package com.soze.idlekluch.kingdom.exception;

import com.soze.idlekluch.game.exception.GameException;

import java.util.Objects;
import java.util.UUID;

public class BuildingDoesNotExistException extends GameException {

  private final String buildingId;

  public BuildingDoesNotExistException(final UUID messageId, final String buildingId) {
    super(messageId);
    this.buildingId = Objects.requireNonNull(buildingId);
  }

  public String getBuildingId() {
    return buildingId;
  }

}
