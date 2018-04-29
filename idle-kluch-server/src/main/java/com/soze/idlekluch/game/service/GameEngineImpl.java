package com.soze.idlekluch.game.service;

import com.soze.idlekluch.game.engine.systems.PhysicsSystem;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.klecs.engine.Engine;
import com.soze.klecs.entity.Entity;
import com.soze.klecs.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GameEngineImpl implements GameEngine {

  private static final Logger LOG = LoggerFactory.getLogger(GameEngineImpl.class);

  private final Engine engine;

  private final ApplicationEventPublisher publisher;

  @Autowired
  public GameEngineImpl(final ApplicationEventPublisher publisher) {
    this.engine = new Engine<>(() -> EntityUUID.randomId());
    this.engine.addSystem(new PhysicsSystem(this.engine));
    this.publisher = Objects.requireNonNull(publisher);
  }

  @PostConstruct
  public void setup() {
    engine.addEntityEventListener(e -> publisher.publishEvent(e));
  }

  @Override
  public void update(float delta) {
    engine.update(delta);
  }

  @Override
  public Entity<EntityUUID> createEmptyEntity() {
    return engine.getEntityFactory().createEntity();
  }

  @Override
  public Entity createEmptyEntity(final EntityUUID entityId) {
    return engine.getEntityFactory().createEntity(entityId);
  }

  @Override
  public void addEntity(Entity entity) {
    Objects.requireNonNull(entity);
    engine.addEntity(entity);
  }

  @Override
  public List<Entity> getAllEntities() {
    return engine.getAllEntities();
  }

  @Override
  public List<Entity> getEntitiesByNode(final Node node) {
    Objects.requireNonNull(node);
    return engine.getEntitiesByNode(node);
  }

  @Override
  public Optional<Entity> getEntity(final EntityUUID entityId) {
    Objects.requireNonNull(entityId);
    return engine.getEntityById(entityId);
  }

}
