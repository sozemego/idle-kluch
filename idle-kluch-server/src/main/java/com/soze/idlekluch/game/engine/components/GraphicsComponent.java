package com.soze.idlekluch.game.engine.components;

import java.util.Objects;

/**
 * Contains data about graphics of an object.
 * This component is not used on the back-end, but it's data
 * is sent to clients.
 */
public class GraphicsComponent extends BaseComponent {

  private String asset;

  public GraphicsComponent() {
    super(ComponentType.GRAPHICS);
  }

  public GraphicsComponent(final String asset) {
    this();
    this.asset = Objects.requireNonNull(asset);
  }

  public String getAsset() {
    return asset;
  }

  public void setAsset(final String asset) {
    this.asset = Objects.requireNonNull(asset);
  }

  @Override
  public GraphicsComponent copy() {
    return new GraphicsComponent(getAsset());
  }

  @Override
  public String toString() {
    return "GraphicsComponent{" +
             "asset='" + asset + '\'' +
             '}';
  }
}
