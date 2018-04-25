package com.soze.idlekluch.game.service;

import com.soze.idlekluch.game.engine.EntityConverter;
import com.soze.idlekluch.game.engine.components.PhysicsComponent;
import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.game.repository.EntityRepository;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.klecs.engine.AddedEntityEvent;
import com.soze.klecs.engine.RemovedEntityEvent;
import com.soze.klecs.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EntityServiceImpl implements EntityService {

  private static final Logger LOG = LoggerFactory.getLogger(EntityServiceImpl.class);

  private final EntityRepository entityRepository;
  private final EntityConverter entityConverter;

  @Autowired
  public EntityServiceImpl(final EntityRepository entityRepository,
                           final EntityConverter entityConverter) {
    this.entityRepository = Objects.requireNonNull(entityRepository);
    this.entityConverter = Objects.requireNonNull(entityConverter);
  }

  @PostConstruct
  public void setup() {
    LOG.info("Initializing [{}]", EntityServiceImpl.class);

    final List<PersistentEntity> persistentEntities = entityRepository.getAllEntities();

    if(persistentEntities.isEmpty()) {
      LOG.info("No entities added, creating one for test.");
      final PersistentEntity persistentEntity = new PersistentEntity();
      persistentEntity.setEntityId(EntityUUID.randomId());
      final PhysicsComponent physicsComponent = new PhysicsComponent();
      physicsComponent.setX(5);
      physicsComponent.setY(6);
      physicsComponent.setWidth(7);
      physicsComponent.setHeight(8);
      persistentEntity.setPhysicsComponent(physicsComponent);
      entityRepository.addEntity(persistentEntity);
    }

  }

  @Override
  public void addEntity(final PersistentEntity entity) {
    Objects.requireNonNull(entity);
    entityRepository.addEntity(entity);
  }

  @Override
  public Optional<PersistentEntity> getEntity(final EntityUUID id) {
    Objects.requireNonNull(id);
    return entityRepository.getEntity(id);
  }

  @Override
  public void deleteEntity(final EntityUUID id) {
    Objects.requireNonNull(id);
    entityRepository.deleteEntity(id);
  }

  @EventListener
  public void handleAddedEntity(final AddedEntityEvent addedEntityEvent) {
    final Entity entity = addedEntityEvent.getEntity();
    LOG.info("Added event for entity ID:[{}]", entity.getId());
    final Optional<PersistentEntity> persistentEntityOptional = getEntity((EntityUUID) entity.getId());
    if(!persistentEntityOptional.isPresent()) {
      final PersistentEntity persistentEntity = entityConverter.convertEntityToPersistent(entity);
      entityRepository.addEntity(persistentEntity);
      LOG.info("Added entity ID:[{}]", entity.getId());
    }
  }

  @EventListener
  public void handleRemovedEntity(final RemovedEntityEvent removedEntityEvent) {
    final Entity entity = removedEntityEvent.getEntity();
    LOG.info("Removed event for entity ID:[{}]", entity.getId());
    entityRepository.deleteEntity((EntityUUID) entity.getId());
  }
}
