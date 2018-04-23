package com.soze.idlekluch.game.engine.components;

import javax.persistence.Embeddable;

@Embeddable
public class PhysicsComponent extends BaseComponent {

  private float x;
  private float y;

  private float width;
  private float height;

  public PhysicsComponent() {
    super(ComponentType.PHYSICS);
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
}
