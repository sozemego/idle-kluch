package com.soze.idlekluch.game.service;

import com.soze.idlekluch.game.engine.components.BaseComponent;
import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.kingdom.events.KingdomRemovedEvent;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.klecs.engine.AddedEntityEvent;
import com.soze.klecs.engine.RemovedEntityEvent;
import com.soze.klecs.entity.Entity;
import com.soze.klecs.node.Node;
import org.springframework.context.event.EventListener;

import java.util.List;
import java.util.Optional;

/**
 * Handles the layer between {@link Entity} and {@link PersistentEntity}.
 * Also loads entity templates from database. The templates are just normal {@link Entity}'s.
 */
public interface EntityService {

  void addEntity(final PersistentEntity entity);

  Optional<PersistentEntity> getEntity(final EntityUUID id);

  void deleteEntity(final EntityUUID id);

  Optional<Entity> getEntityTemplate(final EntityUUID templateId);

  List<Entity> getEntityTemplates();

  List<Entity> getEntitiesByNode(final Node node);

  /**
   * Each component in the source is copied ({@link BaseComponent#copy}).
   * Each component is assigned the id of target.
   */
  void copyEntity(final Entity source, final Entity target);

  @EventListener
  void handleAddedEntity(final AddedEntityEvent addedEntityEvent);

  @EventListener
  void handleRemovedEntity(final RemovedEntityEvent removedEntityEvent);

  @EventListener
  void handleKingdomRemovedEvent(final KingdomRemovedEvent kingdomRemovedEvent);

}
