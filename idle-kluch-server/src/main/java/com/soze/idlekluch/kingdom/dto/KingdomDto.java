package com.soze.idlekluch.kingdom.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class KingdomDto {

  private final String name;
  private final String owner;
  private final String createdAt;

  @JsonCreator
  public KingdomDto(@JsonProperty("name") final String name,
                    @JsonProperty("owner") final String owner,
                    @JsonProperty("createdAt") final String createdAt) {
    this.name = Objects.requireNonNull(name);
    this.owner = Objects.requireNonNull(owner);
    this.createdAt = Objects.requireNonNull(createdAt);
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
}
