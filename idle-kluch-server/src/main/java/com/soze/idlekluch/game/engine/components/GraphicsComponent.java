package com.soze.idlekluch.game.engine.components;

import javax.persistence.Embeddable;
import java.util.Objects;

/**
 * Contains data about graphics of an object.
 * This component is not used on the back-end, but it's data
 * is sent to clients.
 */
@Embeddable
public class GraphicsComponent extends BaseComponent {

  private String asset;

  public GraphicsComponent() {
    super(ComponentType.GRAPHICS);
  }

  public GraphicsComponent(String asset) {
    super(ComponentType.GRAPHICS);
    this.asset = Objects.requireNonNull(asset);
  }

  public String getAsset() {
    return asset;
  }

  public void setAsset(final String asset) {
    this.asset = Objects.requireNonNull(asset);
  }
}
