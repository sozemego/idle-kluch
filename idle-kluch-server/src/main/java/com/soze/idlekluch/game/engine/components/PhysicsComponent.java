package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;

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
  private int width;
  @Column(name = "height")
  private int height;

  public PhysicsComponent() {
    super(ComponentType.PHYSICS);
  }

  public PhysicsComponent(final EntityUUID entityId, final float x, final float y, final int width, final int height) {
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

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  @Override
  public PhysicsComponent copy() {
    return new PhysicsComponent(getEntityId(), getX(), getY(), getWidth(), getHeight());
  }

  @Override
  public String toString() {
    return "PhysicsComponent{" +
             "x=" + x +
             ", y=" + y +
             ", width=" + width +
             ", height=" + height +
             '}';
  }
}
