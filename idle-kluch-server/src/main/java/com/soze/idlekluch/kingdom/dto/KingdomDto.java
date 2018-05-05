package com.soze.idlekluch.kingdom.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.soze.idlekluch.world.entity.TileId;

import java.util.Objects;

public class KingdomDto {

  private final String name;
  private final String owner;
  private final String createdAt;
  private final TileId startingPoint;

  @JsonCreator
  public KingdomDto(@JsonProperty("name") final String name,
                    @JsonProperty("owner") final String owner,
                    @JsonProperty("createdAt") final String createdAt,
                    @JsonProperty("startingPoint") final TileId startingPoint) {
    this.name = Objects.requireNonNull(name);
    this.owner = Objects.requireNonNull(owner);
    this.createdAt = Objects.requireNonNull(createdAt);
    this.startingPoint = Objects.requireNonNull(startingPoint);
  }

  public String getName() {
    return name;
  }

  public String getOwner() {
    return owner;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public TileId getStartingPoint() {
    return startingPoint;
  }
}
