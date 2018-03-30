package com.soze.idlekluch.kingdom.dto;


import com.fasterxml.jackson.annotation.*;

import java.util.Objects;

import static com.soze.idlekluch.kingdom.dto.BuildingDto.*;

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
  private final int width;
  private final int height;
  private final String asset;

  @JsonCreator
  public BuildingDefinitionDto(@JsonProperty("id") final String id,
                               @JsonProperty("name") final String name,
                               @JsonProperty("name") final BuildingType type,
                               @JsonProperty("width") final int width,
                               @JsonProperty("height") final int height,
                               @JsonProperty("asset") final String asset) {
    this.id = Objects.requireNonNull(id);
    this.name = Objects.requireNonNull(name);
    this.type = Objects.requireNonNull(type);
    if(width < 0 || height < 0) {
      throw new IllegalStateException("Width or height cannot be negative");
    }
    this.width = width;
    this.height = height;
    this.asset = Objects.requireNonNull(asset);
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

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public String getAsset() {
    return asset;
  }

  @Override
  public String toString() {
    return "BuildingDefinitionDto{" +
      "id='" + id + '\'' +
      ", name='" + name + '\'' +
      ", type=" + type +
      ", width=" + width +
      ", height=" + height +
      ", asset='" + asset + '\'' +
      '}';
  }
}
