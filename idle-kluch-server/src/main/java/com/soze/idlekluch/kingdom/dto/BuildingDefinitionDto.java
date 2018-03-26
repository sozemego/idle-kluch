package com.soze.idlekluch.kingdom.dto;


import com.fasterxml.jackson.annotation.*;

import java.util.Objects;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "type",
  visible = true
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = WarehouseDefinitionDto.class, name = "WAREHOUSE")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BuildingDefinitionDto {

  private final String id;
  private final String name;
  private final BuildingType type;

  @JsonCreator
  public BuildingDefinitionDto(@JsonProperty("id") final String id,
                               @JsonProperty("name") final String name,
                               @JsonProperty("name") final BuildingType type) {
    this.id = Objects.requireNonNull(id);
    this.name = Objects.requireNonNull(name);
    this.type = Objects.requireNonNull(type);
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public BuildingType getType() {
    return type;
  }

  @Override
  public String toString() {
    return "BuildingDefinitionDto{" +
      "id='" + id + '\'' +
      ", name='" + name + '\'' +
      ", type=" + type +
      '}';
  }

  public enum BuildingType {
    WAREHOUSE
  }

}
