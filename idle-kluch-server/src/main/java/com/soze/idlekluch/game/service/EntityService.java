package com.soze.idlekluch.game.service;

import com.soze.idlekluch.game.engine.components.BaseComponent;
import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.klecs.entity.Entity;

import java.util.List;
import java.util.Optional;

public interface EntityService {

  void addEntity(final PersistentEntity entity);

  Optional<PersistentEntity> getEntity(final EntityUUID id);

  void deleteEntity(final EntityUUID id);

  Optional<Entity> getEntityTemplate(final EntityUUID templateId);

  List<Entity> getEntityTemplates();

  /**
   * Each component in the source is copied ({@link BaseComponent#copy}).
   * Each component is assigned the id of target.
   */
  void copyEntity(final Entity source, final Entity target);

}
