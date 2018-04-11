package com.soze.idlekluch.game.service;

import com.soze.idlekluch.game.engine.systems.PhysicsSystem;
import com.soze.klecs.engine.Engine;
import com.soze.klecs.entity.Entity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class GameEngineImpl implements GameEngine {

  private final Engine engine;

  public GameEngineImpl() {
    this.engine = new Engine();
    this.engine.addSystem(new PhysicsSystem(this.engine));

  }

  @Override
  public void update(float delta) {
    engine.update(delta);
  }

  @Override
  public Entity createEmptyEntity() {
    return engine.getEntityFactory().createEntity();
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

}
