package com.soze.idlekluch.game.engine;

import com.soze.idlekluch.core.aop.annotations.Profiled;
import com.soze.klecs.engine.Engine;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * Thread responsible for running the {@link Engine}.
 */
public class EngineRunner implements Runnable {

  private final Engine engine;
  private float delta;
  private boolean stopFlag = false;
  private boolean engineRunning = false;

  private final Queue<Runnable> actions = new ConcurrentLinkedQueue<>();

  private long updates = 0;

  public EngineRunner(final Engine engine, final float delta) {
    this.engine = Objects.requireNonNull(engine);
    this.delta = delta;
  }

  public void start() {
    this.engineRunning = true;
  }

  public void stop() {
    this.engineRunning = false;
  }

  /**
   * Sets the flag for this runnable to end.
   */
  public void dispose() {
    stopFlag = true;
  }

  public void setDelta(final float delta) {
    this.delta = delta;
  }

  public boolean isEngineRunning() {
    return engineRunning;
  }

  public long updates() {
    return updates;
  }

  /**
   * Adds an action to run after engine has updated.
   */
  public void addAction(final Runnable action) {
    Objects.requireNonNull(action);
    actions.add(action);
  }

  @Override
  @Profiled
  public void run() {
    long currentTime = System.nanoTime();
    double accumulator = 0;

    while(!stopFlag) {

      //so that it's value is not changed with setDelta method call between update and calculating sleep time
      final float deltaToUse = delta;
      final long now = System.nanoTime();
      final long frameTime = now - currentTime;
      accumulator += TimeUnit.NANOSECONDS.toMillis(frameTime) / 1000f;
      if(accumulator >= deltaToUse * 3) {
        System.out.println(accumulator);
      }
      currentTime = now;

      while(accumulator >= deltaToUse) {
        if(engineRunning) {
          runOnce(deltaToUse);
        }
        accumulator -= deltaToUse;
      }

      runActions();

      try {
        Thread.sleep(Math.max(0, (int) (deltaToUse * 1000) - TimeUnit.NANOSECONDS.toMillis(frameTime)));
      } catch (final InterruptedException e) {
        e.printStackTrace();
        stopFlag = true;
        System.out.println("STOPPING ENGINE RUNNER");
      }

    }
  }

  private void runOnce(final float delta) {
    engine.update(delta);
    ++updates;
  }

  private void runActions() {
    Runnable action;
    while((action = actions.poll()) != null) {
      action.run();
    }
  }

}
