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
  private final String asset;

  private final int x;
  private final int y;

  private final String type;

  @JsonCreator
  public BuildingDto(@JsonProperty("buildingId") final String buildingId,
                     @JsonProperty("createdAt") final String createdAt,
                     @JsonProperty("name") final String name,
                     @JsonProperty("asset") final String asset,
                     @JsonProperty("x") final int x,
                     @JsonProperty("y") final int y,
                     @JsonProperty("type") final BuildingType type) {
    this.buildingId = Objects.requireNonNull(buildingId);
    this.createdAt = Objects.requireNonNull(createdAt);
    this.name = Objects.requireNonNull(name);
    this.asset = Objects.requireNonNull(asset);
    this.x = x;
    this.y = y;
    this.type = Objects.requireNonNull(type.toString());
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

  public String getType() {
    return type;
  }

  public String getAsset() {
    return asset;
  }

  public enum BuildingType {
    WAREHOUSE
  }

}
