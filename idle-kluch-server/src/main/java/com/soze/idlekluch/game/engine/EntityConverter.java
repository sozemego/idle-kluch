package com.soze.idlekluch.game.engine;

import com.soze.idlekluch.game.engine.components.BaseComponent;
import com.soze.idlekluch.game.engine.components.GraphicsComponent;
import com.soze.idlekluch.game.engine.components.OwnershipComponent;
import com.soze.idlekluch.game.engine.components.PhysicsComponent;
import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.game.message.EntityMessage;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.kingdom.service.BuildingService;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.klecs.entity.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Converts database Entities to ECS Entities.
 */
@Service
public class EntityConverter {

  private final GameEngine gameEngine;
  private final BuildingService buildingService;

  @Autowired
  public EntityConverter(final GameEngine gameEngine, final BuildingService buildingService) {
    this.gameEngine = Objects.requireNonNull(gameEngine);
    this.buildingService = Objects.requireNonNull(buildingService);
  }

  public Entity convertPersistentToEntity(final PersistentEntity persistentEntity) {
    Objects.requireNonNull(persistentEntity);

    final Entity entity = gameEngine.createEmptyEntity(persistentEntity.getEntityId());
    entity.addComponent(persistentEntity.getGraphicsComponent());
    entity.addComponent(persistentEntity.getPhysicsComponent());

    return entity;
  }

  public PersistentEntity convertEntityToPersistent(final Entity entity) {
    Objects.requireNonNull(entity);

    final PersistentEntity persistentEntity = new PersistentEntity();
    persistentEntity.setEntityId((EntityUUID) entity.getId());
    persistentEntity.setGraphicsComponent((GraphicsComponent) entity.getComponent(GraphicsComponent.class));
    persistentEntity.setPhysicsComponent((PhysicsComponent) entity.getComponent(PhysicsComponent.class));
    persistentEntity.setOwnershipComponent((OwnershipComponent) entity.getComponent(OwnershipComponent.class));

    return persistentEntity;
  }

  public EntityMessage toMessage(final Entity entity) {
    final List<BaseComponent> components = entity.getAllComponents(BaseComponent.class);
    return new EntityMessage((EntityUUID)entity.getId(), components);
  }

}
