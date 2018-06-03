package com.soze.idlekluch.game.repository;

import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import java.util.List;
import java.util.Optional;

public interface EntityRepository {

  void addEntity(PersistentEntity entity);

  Optional<PersistentEntity> getEntity(EntityUUID id);

  boolean entityExists(EntityUUID id);

  void deleteEntity(EntityUUID id);

  List<PersistentEntity> getAllEntities();

  List<PersistentEntity> getAllEntityTemplates();

}
