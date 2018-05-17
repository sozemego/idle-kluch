package com.soze.idlekluch.game.engine;

import com.soze.idlekluch.game.engine.components.PhysicsComponent;

import java.awt.*;
import java.util.Objects;

public class EntityUtils {

  private EntityUtils() {
    throw new IllegalStateException("Cannot instantiate EntityUtils");
  }

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


}
