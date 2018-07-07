package com.soze.idlekluch.game.engine;

import com.soze.idlekluch.game.engine.components.NameComponent;
import com.soze.idlekluch.game.engine.components.PhysicsComponent;
import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.klecs.entity.Entity;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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


  public static float distance(final Entity entity, final Point point) {
    Objects.requireNonNull(entity);
    Objects.requireNonNull(point);

    final PhysicsComponent physicsComponent = entity.getComponent(PhysicsComponent.class);
    return (float) Math.hypot(physicsComponent.getX() - point.x, physicsComponent.getY() - point.y);
  }

  public static float distance(final Entity entity1, final Entity entity2) {
    Objects.requireNonNull(entity1);
    Objects.requireNonNull(entity2);

    final PhysicsComponent physicsComponent1 = entity1.getComponent(PhysicsComponent.class);
    final PhysicsComponent physicsComponent2 = entity2.getComponent(PhysicsComponent.class);
    return (float) Math.hypot(
      physicsComponent1.getX() - physicsComponent2.getX(),
      physicsComponent1.getY() - physicsComponent2.getY()
    );
  }

  public static Optional<Entity> getClosestEntity(final Entity entity, final List<Entity> entities) {
    if (entities.isEmpty()) {
      return Optional.empty();
    }

    if(entities.size() == 1) {
      return Optional.of(entities.get(0));
    }

    return entities
             .stream()
             .sorted(Comparator.comparingInt(e -> (int) distance(e, entity)))
             .findFirst();
  }

  /**
   * Returns name of the entity from {@link NameComponent}.
   * All entities must have this component, if one doesn't, an {@link IllegalStateException} is thrown.
   */
  public static String getName(final Entity entity) {
    Objects.requireNonNull(entity);

    final NameComponent nameComponent = entity.getComponent(NameComponent.class);
    if(nameComponent == null) {
      throw new IllegalStateException("Entity with id " + entity.getId() + " does not have name component.");
    }

    return nameComponent.getName();
  }

  /**
   * Returns name of the entity from {@link NameComponent}.
   * All entities must have this component, if one doesn't, an {@link IllegalStateException} is thrown.
   */
  public static String getName(final PersistentEntity persistentEntity) {
    Objects.requireNonNull(persistentEntity);

    final NameComponent nameComponent = persistentEntity.getNameComponent();
    if(nameComponent == null) {
      throw new IllegalStateException("Entity with id " + persistentEntity.getEntityId() + " does not have name component.");
    }

    return nameComponent.getName();
  }
}
