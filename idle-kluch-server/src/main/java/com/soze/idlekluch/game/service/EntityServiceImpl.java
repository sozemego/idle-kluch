package com.soze.idlekluch.game.service;

import com.soze.idlekluch.game.engine.EntityConverter;
import com.soze.idlekluch.game.engine.components.BaseComponent;
import com.soze.idlekluch.game.engine.components.OwnershipComponent;
import com.soze.idlekluch.game.engine.nodes.Nodes;
import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.game.repository.EntityRepository;
import com.soze.idlekluch.kingdom.events.KingdomRemovedEvent;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.klecs.engine.AddedEntityEvent;
import com.soze.klecs.engine.RemovedEntityEvent;
import com.soze.klecs.entity.Entity;
import com.soze.klecs.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntityServiceImpl implements EntityService {

  private static final Logger LOG = LoggerFactory.getLogger(EntityServiceImpl.class);

  private final GameEngine gameEngine;
  private final EntityRepository entityRepository;
  private final EntityConverter entityConverter;

  private final Map<EntityUUID, Entity> entityTemplates = new HashMap<>();

  @Value("entities.json")
  private ClassPathResource entityData;

  @Autowired
  public EntityServiceImpl(final GameEngine gameEngine,
                           final EntityRepository entityRepository,
                           final EntityConverter entityConverter) {
    this.gameEngine = Objects.requireNonNull(gameEngine);
    this.entityRepository = Objects.requireNonNull(entityRepository);
    this.entityConverter = Objects.requireNonNull(entityConverter);
  }

  @PostConstruct
  public void setup() {
    loadEntityTemplates();
    loadExistingEntitiesFromDB();
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

  @Override
  public Optional<Entity> getEntityTemplate(final EntityUUID templateId) {
    Objects.requireNonNull(templateId);
    return Optional.ofNullable(entityTemplates.get(templateId));
  }

  @Override
  public List<Entity> getEntityTemplates() {
    return new ArrayList<>(entityTemplates.values());
  }

  @Override
  public List<Entity> getEntitiesByNode(final Node node) {
    Objects.requireNonNull(node);
    return gameEngine.getEntitiesByNode(node);
  }

  @Override
  public void copyEntity(final Entity source, final Entity target) {
    source.getAllComponents(BaseComponent.class)
      .stream()
      .map(BaseComponent::copy)
      .forEach(component -> {
        component.setEntityId((EntityUUID) target.getId());
        target.addComponent(component);
      });
  }

  @Override
  @EventListener
  public void handleAddedEntity(final AddedEntityEvent addedEntityEvent) {
    final Entity entity = addedEntityEvent.getEntity();
    LOG.info("Added event for entity ID:[{}]", entity.getId());
    final boolean entityExists = entityRepository.entityExists((EntityUUID) entity.getId());
    LOG.info("Entity [{}] is already present [{}]", entity.getId(), entityExists);
    if(!entityExists) {
      final PersistentEntity persistentEntity = entityConverter.convertEntityToPersistent(entity);
      entityRepository.addEntity(persistentEntity);
      LOG.info("Added entity ID: [{}]", entity.getId());
    }
  }

  @Override
  @EventListener
  public void handleRemovedEntity(final RemovedEntityEvent removedEntityEvent) {
    final Entity entity = removedEntityEvent.getEntity();
    LOG.info("Removed event for entity ID: [{}]", entity.getId());
    entityRepository.deleteEntity((EntityUUID) entity.getId());
  }

  @Override
  @EventListener
  public void handleKingdomRemovedEvent(final KingdomRemovedEvent kingdomRemovedEvent) {
    final EntityUUID kingdomId = kingdomRemovedEvent.getKingdomId();

    final List<Entity> entities = gameEngine
                                    .getEntitiesByNode(Nodes.OWNERSHIP)
                                    .stream()
                                    .filter(entity -> {
                                      final OwnershipComponent ownershipComponent = entity.getComponent(OwnershipComponent.class);
                                      return kingdomId.equals(ownershipComponent.getOwnerId());
                                    })
                                    .collect(Collectors.toList());

    entities.forEach(entity -> gameEngine.deleteEntity((EntityUUID) entity.getId()));
  }

  private void loadEntityTemplates() {
    final List<PersistentEntity> templates = entityRepository.getAllEntityTemplates();
    LOG.info("Found [{}] entity templates", templates.size());
    templates.forEach(template -> entityTemplates.put(template.getEntityId(), entityConverter.convertPersistentToEntity(template)));
  }

  private void loadExistingEntitiesFromDB() {
    final List<PersistentEntity> persistentEntities = entityRepository.getAllEntities();
    final List<Entity> entities = persistentEntities
                                    .stream()
                                    .peek(e -> LOG.info("Converting persistent entity with ID:[{}] to ECS Entity", e.getEntityId()))
                                    .map(entityConverter::convertPersistentToEntity)
                                    .peek(gameEngine::addEntity)
                                    .collect(Collectors.toList());

    LOG.info("Loaded and converted [{}] persistent entities", entities.size());
  }
}
