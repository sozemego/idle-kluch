package com.soze.idlekluch.game.repository;

import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import java.util.List;
import java.util.Optional;

public interface EntityRepository {

  void addEntity(PersistentEntity entity);

  void updateEntities(List<PersistentEntity> entities);

  Optional<PersistentEntity> getEntity(EntityUUID id);

  List<PersistentEntity> getEntities(List<EntityUUID> entityIds);

  boolean entityExists(EntityUUID id);

  void deleteEntity(EntityUUID id);

  List<PersistentEntity> getAllEntities();

  List<PersistentEntity> getAllEntityTemplates();

}
