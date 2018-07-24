package com.soze.idlekluch.kingdom.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.soze.idlekluch.world.entity.TileId;

import java.util.Objects;

public class KingdomDto {

  private final String id;
  private final String name;
  private final String owner;
  private final String createdAt;
  private final TileId startingPoint;
  private final long idleBucks;

  @JsonCreator
  public KingdomDto(@JsonProperty("id") String id,
                    @JsonProperty("name") final String name,
                    @JsonProperty("owner") final String owner,
                    @JsonProperty("createdAt") final String createdAt,
                    @JsonProperty("startingPoint") final TileId startingPoint,
                    @JsonProperty("idleBucks") final long idleBucks) {
    this.id = Objects.requireNonNull(id);
    this.name = Objects.requireNonNull(name);
    this.owner = Objects.requireNonNull(owner);
    this.createdAt = Objects.requireNonNull(createdAt);
    this.startingPoint = Objects.requireNonNull(startingPoint);
    this.idleBucks = idleBucks;
  }

  public String getId() {
    return id;
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

  public long getIdleBucks() {
    return idleBucks;
  }
}
