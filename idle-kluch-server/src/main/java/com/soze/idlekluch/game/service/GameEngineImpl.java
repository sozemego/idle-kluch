package com.soze.idlekluch.game.service;

import com.soze.idlekluch.core.aop.annotations.Profiled;
import com.soze.idlekluch.core.event.AppStartedEvent;
import com.soze.idlekluch.core.event.EventPublisher;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.EngineRunner;
import com.soze.idlekluch.game.engine.systems.PhysicsSystem;
import com.soze.idlekluch.game.engine.systems.ResourceHarvesterSystem;
import com.soze.idlekluch.world.service.ResourceService;
import com.soze.klecs.engine.Engine;
import com.soze.klecs.entity.Entity;
import com.soze.klecs.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class GameEngineImpl implements GameEngine {

  private static final Logger LOG = LoggerFactory.getLogger(GameEngineImpl.class);
  private final ExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  private final EventPublisher publisher;

  private final Engine engine;
  private final EngineRunner engineRunner;
  private final Map<EntityUUID, Entity> changedEntities = new HashMap<>();

  private final boolean isIntegrationTest;

  @Autowired
  public GameEngineImpl(final EventPublisher publisher,
                        final Environment environment,
                        final WebSocketMessagingService webSocketMessagingService,
                        final ResourceService resourceService) {

    this.engine = new Engine(EntityUUID::randomId);
    this.engine.addSystem(new PhysicsSystem(this.engine, this.changedEntities));
    this.engine.addSystem(new ResourceHarvesterSystem(this.engine, this.changedEntities, webSocketMessagingService, resourceService));

    this.publisher = Objects.requireNonNull(publisher);

    this.isIntegrationTest = environment.acceptsProfiles("integration-test");
    this.engineRunner = new EngineRunner(engine, 1f / 60f);
  }

  @PostConstruct
  public void setup() {
    engine.addEntityEventListener(publisher::publishEvent);
  }

  @PreDestroy
  public void preDestroy() {
    this.engineRunner.stop();
    this.engineRunner.dispose();
  }

  @Override
  public void handleAppStartedEvent(final AppStartedEvent event) {
    LOG.info("App started, starting game engine.");
    if(!isIntegrationTest) {
      executor.execute(this.engineRunner);
      this.engineRunner.start();
    }
  }

  @PreDestroy
  public void dispose() {
    engineRunner.dispose();
    executor.shutdown();
  }

  @Override
  public void update(float delta) {
    engine.update(delta);
  }

  @Override
  public void start() {
    engineRunner.start();
  }

  @Override
  public void stop() {
    engineRunner.stop();
  }

  @Override
  public boolean isPaused() {
    return !engineRunner.isEngineRunning();
  }

  @Override
  public void setDelta(final float delta) {
    engineRunner.setDelta(delta);
  }

  @Override
  public Entity createEmptyEntity() {
    return engine.getEntityFactory().createEntity();
  }

  @Override
  public Entity createEmptyEntity(final EntityUUID entityId) {
    return engine.getEntityFactory().createEntity(entityId);
  }

  @Override
  public void addEntity(final Entity entity) {
    Objects.requireNonNull(entity);
    engine.addEntity(entity);
  }

  @Override
  public List<Entity> getAllEntities() {
    return engine.getAllEntities();
  }

  @Override
  public Collection<Entity> getAllEntitiesCollection() {
    return engine.getAllEntitiesCollection();
  }

  @Override
  @Profiled
  public List<Entity> getEntitiesByNode(final Node node) {
    Objects.requireNonNull(node);
    return engine.getEntitiesByNode(node);
  }

  @Override
  public Optional<Entity> getEntity(final EntityUUID entityId) {
    Objects.requireNonNull(entityId);
    return engine.getEntityById(entityId);
  }

  @Override
  public void deleteEntity(final EntityUUID entityId) {
    Objects.requireNonNull(entityId);
    engine.removeEntity(entityId);
  }

  @Override
  public void reset() {
    engine
      .getAllEntities()
      .forEach(entity -> engine.removeEntity(entity.getId()));
  }

  @Override
  public void addAction(final Runnable action) {
    Objects.requireNonNull(action);
    engineRunner.addAction(action);
  }

  @Override
  public Map<EntityUUID, Entity> getChangedEntities() {
    return changedEntities;
  }

}
