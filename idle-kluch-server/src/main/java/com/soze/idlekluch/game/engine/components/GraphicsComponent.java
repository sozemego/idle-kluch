package com.soze.idlekluch.game.engine.components;

import java.util.Objects;

/**
 * Contains data about graphics of an object.
 * This component is not used on the back-end, but it's data
 * is sent to clients.
 */
public class GraphicsComponent extends BaseComponent {

  private final String asset;

  public GraphicsComponent(String asset) {
    super(ComponentType.GRAPHICS);
    this.asset = Objects.requireNonNull(asset);
  }

  public String getAsset() {
    return asset;
  }
}