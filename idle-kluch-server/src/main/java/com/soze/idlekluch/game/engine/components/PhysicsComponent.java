package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "physics_components")
public class PhysicsComponent extends BaseComponent {

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
    setEntityId(entityId);
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
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
