package com.soze.idlekluch.game.repository;

import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.utils.jpa.EntityUUID;

import java.util.Optional;

public interface EntityRepository {

  void addEntity(final PersistentEntity entity);

  Optional<PersistentEntity> getEntity(final EntityUUID id);

  void deleteEntity(final EntityUUID id);

}
