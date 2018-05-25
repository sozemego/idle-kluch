package com.soze.idlekluch.game.engine;

import com.soze.idlekluch.game.engine.components.PhysicsComponent;
import com.soze.klecs.entity.Entity;

import java.awt.*;
import java.util.Objects;

public class EntityUtils {

  private EntityUtils() {
    throw new IllegalStateException("Cannot instantiate EntityUtils");
  }

  /**
   * Returns true if the bounds of one entity intersect with bounds of another.
   * The bounds are rectangles.
   */
  public static boolean doesCollide(final PhysicsComponent physicsComponent1, final PhysicsComponent physicsComponent2) {
    Objects.requireNonNull(physicsComponent1);
    Objects.requireNonNull(physicsComponent2);

    final Rectangle rectangle1 = new Rectangle(
        (int) physicsComponent1.getX(),
        (int) physicsComponent1.getY(),
        (int) physicsComponent1.getWidth(),
        (int) physicsComponent1.getHeight()
    );

    final Rectangle rectangle2 = new Rectangle(
        (int) physicsComponent2.getX(),
        (int) physicsComponent2.getY(),
        (int) physicsComponent2.getWidth(),
        (int) physicsComponent2.getHeight()
    );

    return rectangle1.intersects(rectangle2);
  }

  public static boolean doesCollide(final Entity entity1, final Entity entity2) {
    Objects.requireNonNull(entity1);
    Objects.requireNonNull(entity2);

    final PhysicsComponent physicsComponent1 = entity1.getComponent(PhysicsComponent.class);
    final PhysicsComponent physicsComponent2 = entity2.getComponent(PhysicsComponent.class);

    if(physicsComponent1 != null && physicsComponent2 != null) {
      return doesCollide(physicsComponent1, physicsComponent2);
    }

    return false;
  }

  /**
   * Checks if given point is within bounds of the entity.
   * The bounds are a rectangle.
   */
  public static boolean intersects(final Entity entity, final Point point) {
    final PhysicsComponent physicsComponent = entity.getComponent(PhysicsComponent.class);

    final Rectangle rectangle = new Rectangle(
      (int) physicsComponent.getX(),
      (int) physicsComponent.getY(),
      (int) physicsComponent.getWidth(),
      (int) physicsComponent.getHeight()
    );

    return rectangle.intersects(new Rectangle(point.x, point.y, 1, 1));
  }

}
