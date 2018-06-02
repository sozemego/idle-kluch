package com.soze.idlekluch.game.engine;

import com.soze.idlekluch.game.engine.components.*;
import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.game.message.EntityMessage;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.utils.jpa.EntityUUID;
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
    persistentEntity.setEntityId((EntityUUID) entity.getId());
    persistentEntity.setGraphicsComponent(entity.getComponent(GraphicsComponent.class));
    persistentEntity.setPhysicsComponent(entity.getComponent(PhysicsComponent.class));
    persistentEntity.setOwnershipComponent(entity.getComponent(OwnershipComponent.class));
    persistentEntity.setNameComponent(entity.getComponent(NameComponent.class));
    persistentEntity.setBuildableComponent(entity.getComponent(BuildableComponent.class));
    persistentEntity.setStaticOccupySpaceComponent(entity.getComponent(StaticOccupySpaceComponent.class));
    persistentEntity.setCostComponent(entity.getComponent(CostComponent.class));
    persistentEntity.setResourceSourceComponent(entity.getComponent(ResourceSourceComponent.class));

    return persistentEntity;
  }

  public EntityMessage toMessage(final Entity entity) {
    final List<BaseComponent> components = entity.getAllComponents(BaseComponent.class);
    return new EntityMessage((EntityUUID)entity.getId(), components);
  }

}
