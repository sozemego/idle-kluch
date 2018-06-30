package com.soze.idlekluch.game.engine;

import com.soze.idlekluch.game.engine.components.*;
import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.game.message.EntityMessage;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.klecs.entity.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Converts database Entities to ECS Entities and vice-versa.
 */
@Service
public class EntityConverter {

  private final GameEngine gameEngine;

  @Autowired
  public EntityConverter(final GameEngine gameEngine) {
    this.gameEngine = Objects.requireNonNull(gameEngine);
  }

  public Entity convertPersistentToEntity(final PersistentEntity persistentEntity) {
    Objects.requireNonNull(persistentEntity);

    final Entity entity = gameEngine.createEmptyEntity(persistentEntity.getEntityId());
    persistentEntity
        .getAllComponents()
        .forEach(entity::addComponent);

    return entity;
  }

  public PersistentEntity convertEntityToPersistent(final Entity entity) {
    Objects.requireNonNull(entity);

    final PersistentEntity persistentEntity = new PersistentEntity();
    return copyEntityToPersistent(entity, persistentEntity);
  }

  public PersistentEntity copyEntityToPersistent(final Entity source, final PersistentEntity target) {
    Objects.requireNonNull(source);
    Objects.requireNonNull(target);

    target.setEntityId((EntityUUID) source.getId());
    target.setGraphicsComponent(source.getComponent(GraphicsComponent.class));
    target.setPhysicsComponent(source.getComponent(PhysicsComponent.class));
    target.setOwnershipComponent(source.getComponent(OwnershipComponent.class));
    target.setNameComponent(source.getComponent(NameComponent.class));
    target.setBuildableComponent(source.getComponent(BuildableComponent.class));
    target.setStaticOccupySpaceComponent(source.getComponent(StaticOccupySpaceComponent.class));
    target.setCostComponent(source.getComponent(CostComponent.class));
    target.setResourceSourceComponent(source.getComponent(ResourceSourceComponent.class));
    target.setResourceHarvesterComponent(source.getComponent(ResourceHarvesterComponent.class));
    target.setResourceStorageComponent(source.getComponent(ResourceStorageComponent.class));

    return target;
  }

  public EntityMessage toMessage(final Entity entity) {
    final List<BaseComponent> components = entity.getAllComponents(BaseComponent.class);
    return new EntityMessage((EntityUUID)entity.getId(), components);
  }

  /**
   * Each component in the source is copied ({@link BaseComponent#copy}).
   * Each component is assigned the id of target.
   */
  public void copyEntity(final Entity source, final Entity target) {
    source.getAllComponents(BaseComponent.class)
      .stream()
      .map(BaseComponent::copy)
      .forEach(component -> {
        component.setEntityId((EntityUUID) target.getId());
        target.addComponent(component);
      });
  }

}
