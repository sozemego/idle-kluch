package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Contains data about graphics of an object.
 * This component is not used on the back-end, but it's data
 * is sent to clients.
 */
@Entity
@Table(name = "graphics_components")
public class GraphicsComponent extends BaseComponent {

  @Column(name = "asset")
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
