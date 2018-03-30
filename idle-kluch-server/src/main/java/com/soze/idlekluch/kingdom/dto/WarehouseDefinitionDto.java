package com.soze.idlekluch.kingdom.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.soze.idlekluch.kingdom.dto.BuildingDto.BuildingType;

public class WarehouseDefinitionDto extends BuildingDefinitionDto {

  private final int capacity;

  @JsonCreator
  public WarehouseDefinitionDto(@JsonProperty("id") final String id,
                                @JsonProperty("name") final String name,
                                @JsonProperty("type") final BuildingType type,
                                @JsonProperty("width") final int width,
                                @JsonProperty("height") final int height,
                                @JsonProperty("asset") final String asset,
                                @JsonProperty("capacity") final int capacity) {
    super(id, name, type, width, height, asset);
    this.capacity = capacity;
  }

  public int getCapacity() {
    return capacity;
  }
}
