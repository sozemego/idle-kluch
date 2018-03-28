package com.soze.idlekluch.kingdom.exception;

import java.util.Objects;

public class BuildingDoesNotExistException extends RuntimeException {

  private final String buildingId;

  public BuildingDoesNotExistException(final String buildingId) {
    this.buildingId = Objects.requireNonNull(buildingId);
  }

  public String getBuildingId() {
    return buildingId;
  }

}
