package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.game.entity.PhysicsComponentType;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@TypeDef(name = "PhysicsComponentType", typeClass = PhysicsComponentType.class)
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

  public PhysicsComponent(final float x, final float y, final int width, final int height) {
    this();
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
    return new PhysicsComponent(getX(), getY(), getWidth(), getHeight());
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
