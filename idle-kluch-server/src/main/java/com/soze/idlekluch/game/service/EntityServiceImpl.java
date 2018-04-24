package com.soze.idlekluch.game.service;

import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.game.repository.EntityRepository;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.klecs.engine.AddedEntityEvent;
import com.soze.klecs.engine.RemovedEntityEvent;
import com.soze.klecs.entity.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class EntityServiceImpl implements EntityService {

  private final EntityRepository entityRepository;

  @Autowired
  public EntityServiceImpl(final EntityRepository entityRepository) {
    this.entityRepository = Objects.requireNonNull(entityRepository);
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
    System.out.println("ADDED ENTITY");
    final Entity entity = addedEntityEvent.getEntity();
    getEntity((EntityUUID) entity.getId()).ifPresent(e -> {
      //TODO convert here
      //and add
    });
  }

  @EventListener
  public void handleRemovedEntity(final RemovedEntityEvent removedEntityEvent) {
    System.out.println("REMOVED ENTITY");
    entityRepository.deleteEntity((EntityUUID) removedEntityEvent.getEntity().getId());
  }
}
