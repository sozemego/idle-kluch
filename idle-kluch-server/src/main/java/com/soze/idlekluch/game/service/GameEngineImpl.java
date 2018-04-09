package com.soze.idlekluch.game.service;

import com.soze.klecs.engine.Engine;
import com.soze.klecs.entity.Entity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GameEngineImpl implements GameEngine {

  private final Engine engine;

  public GameEngineImpl() {
    this.engine = new Engine();
  }

  @Override
  public void update(float delta) {
    engine.update(delta);
  }

  @Override
  public void addEntity(Entity entity) {
    Objects.requireNonNull(entity);
    engine.addEntity(entity);
  }

}
