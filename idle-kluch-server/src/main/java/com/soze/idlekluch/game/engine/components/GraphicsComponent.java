package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.*;
import java.util.Objects;

/**
 * Contains data about graphics of an object.
 * This component is not used on the back-end, but it's data
 * is sent to clients.
 */
@Entity
@Table(name = "graphics_components")
public class GraphicsComponent extends BaseComponent {

  @EmbeddedId
  @AttributeOverride(name = "id", column = @Column(name = "entity_id"))
  private EntityUUID entityId;

  @Column(name = "asset")
  private String asset;

  public GraphicsComponent() {
    super(ComponentType.GRAPHICS);
  }

  public GraphicsComponent(final EntityUUID entityId, final String asset) {
    this();
    this.entityId = entityId;
    this.asset = Objects.requireNonNull(asset);
  }

  public GraphicsComponent(String asset) {
    super(ComponentType.GRAPHICS);
    this.asset = Objects.requireNonNull(asset);
  }

  public EntityUUID getEntityId() {
    return entityId;
  }

  public void setEntityId(final EntityUUID entityId) {
    this.entityId = entityId;
  }

  public String getAsset() {
    return asset;
  }

  public void setAsset(final String asset) {
    this.asset = Objects.requireNonNull(asset);
  }

  @Override
  public GraphicsComponent copy() {
    return new GraphicsComponent(getEntityId(), getAsset());
  }
}
