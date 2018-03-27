package com.soze.idlekluch.kingdom.dto;

import com.fasterxml.jackson.annotation.*;

import java.util.Objects;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "type",
  visible = true
)
@JsonSubTypes(
  @JsonSubTypes.Type(value = WarehouseDto.class, name = "WAREHOUSE")
)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BuildingDto {

  private final String buildingId;
  private final String createdAt;
  private final String name;

  private final int x;
  private final int y;

  @JsonCreator
  public BuildingDto(@JsonProperty("buildingId") final String buildingId,
                     @JsonProperty("createdAt") final String createdAt,
                     @JsonProperty("name") final String name,
                     @JsonProperty("x") final int x,
                     @JsonProperty("y") final int y) {
    this.buildingId = Objects.requireNonNull(buildingId);
    this.createdAt = Objects.requireNonNull(createdAt);
    this.name = Objects.requireNonNull(name);
    this.x = x;
    this.y = y;
  }

  public String getBuildingId() {
    return buildingId;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public String getName() {
    return name;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public enum BuildingType {
    WAREHOUSE
  }

}
