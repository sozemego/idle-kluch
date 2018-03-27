package com.soze.idlekluch.kingdom.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.soze.idlekluch.kingdom.entity.StorageUnit;

import java.util.List;
import java.util.Objects;

public class WarehouseDto extends BuildingDto {

  private final List<StorageUnit> storageUnits;

  @JsonCreator
  public WarehouseDto(@JsonProperty("buildingId") final String buildingId,
                      @JsonProperty("createdAt") final String createdAt,
                      @JsonProperty("name") final String name,
                      @JsonProperty("x") final int x,
                      @JsonProperty("y") final int y,
                      @JsonProperty("storageUnits") final List<StorageUnit> storageUnits) {
    super(buildingId, createdAt, name, x, y);
    this.storageUnits = Objects.requireNonNull(storageUnits);
  }

  public List<StorageUnit> getStorageUnits() {
    return storageUnits;
  }
}
