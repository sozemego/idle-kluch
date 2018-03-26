package com.soze.idlekluch.kingdom.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WarehouseDefinitionDto extends BuildingDefinitionDto {

  @JsonCreator
  public WarehouseDefinitionDto(@JsonProperty("id") final String id,
                                @JsonProperty("name") final String name,
                                @JsonProperty("type") final BuildingType type) {
    super(id, name, type);
  }

}
