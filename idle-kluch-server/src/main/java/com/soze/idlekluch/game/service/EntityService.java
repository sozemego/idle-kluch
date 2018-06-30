package com.soze.idlekluch.game.service;

import com.soze.idlekluch.game.engine.components.BaseComponent;
import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.kingdom.events.KingdomRemovedEvent;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
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

  void addEntity(PersistentEntity entity);

  Optional<PersistentEntity> getEntity(EntityUUID id);

  void deleteEntity(EntityUUID id);

  Optional<Entity> getEntityTemplate(EntityUUID templateId);

  List<Entity> getEntityTemplates();

  List<Entity> getEntitiesByNode(Node node);

  void handleAddedEntity(AddedEntityEvent addedEntityEvent);

  void handleRemovedEntity(RemovedEntityEvent removedEntityEvent);

  void handleKingdomRemovedEvent(KingdomRemovedEvent kingdomRemovedEvent);

  void persistEntities();

}
