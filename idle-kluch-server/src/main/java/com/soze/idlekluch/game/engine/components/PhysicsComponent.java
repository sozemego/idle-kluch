package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "physics_components")
public class PhysicsComponent extends BaseComponent {

  @EmbeddedId
  @AttributeOverride(name = "id", column = @Column(name = "entity_id"))
  private EntityUUID entityId;

  @Column(name = "x")
  private float x;
  @Column(name = "y")
  private float y;

  @Column(name = "width")
  private float width;
  @Column(name = "height")
  private float height;

  public PhysicsComponent() {
    super(ComponentType.PHYSICS);
  }

  public PhysicsComponent(final EntityUUID entityId, final float x, final float y, final float width, final float height) {
    this();
    this.entityId = Objects.requireNonNull(entityId);
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public EntityUUID getEntityId() {
    return entityId;
  }

  public void setEntityId(final EntityUUID entityId) {
    this.entityId = Objects.requireNonNull(entityId);
  }

  public float getX() {
    return x;
  }

  public void setX(float x) {
    this.x = x;
  }

  public float getY() {
    return y;
  }

  public void setY(float y) {
    this.y = y;
  }

  public float getWidth() {
    return width;
  }

  public void setWidth(float width) {
    this.width = width;
  }

  public float getHeight() {
    return height;
  }

  public void setHeight(float height) {
    this.height = height;
  }

  @Override
  public PhysicsComponent copy() {
    return new PhysicsComponent(getEntityId(), getX(), getY(), getWidth(), getHeight());
  }
}
