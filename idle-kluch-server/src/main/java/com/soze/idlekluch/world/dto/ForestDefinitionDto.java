package com.soze.idlekluch.world.dto;

import java.util.Objects;

public class ForestDefinitionDto {

  private final String id;
  private final String asset;
  private final int width;
  private final int height;

  public ForestDefinitionDto(final String id, final String asset, final int width, final int height) {
    this.id = Objects.requireNonNull(id);
    this.asset = Objects.requireNonNull(asset);
    this.width = width;
    this.height = height;
  }

  public String getId() {
    return id;
  }

  public String getAsset() {
    return asset;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
