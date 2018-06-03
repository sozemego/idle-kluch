package com.soze.idlekluch.game.engine;

import com.soze.idlekluch.core.aop.annotations.Profiled;
import com.soze.klecs.engine.Engine;

import java.util.Objects;

/**
 * Thread responsible for running the {@link Engine}.
 */
public class EngineRunner implements Runnable {

  private final Engine engine;
  private float delta;
  private boolean stopFlag = false;
  private boolean engineRunning = false;

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

  public long updates() {
    return updates;
  }

  @Override
  @Profiled
  public void run() {
    while(!stopFlag) {

      //so that it's value is not changed with setDelta method call between update and calculating sleep time
      final float deltaToUse = delta;

      final long totalTime = engineRunning ? runOnce(deltaToUse) : 0;
      final long sleepTime = ((int) (deltaToUse * 1000)) - totalTime;

      try {
        Thread.sleep(Math.max(0, sleepTime));
      } catch (InterruptedException e) {
        e.printStackTrace();
        stopFlag = true;
      }

    }
  }

  private long runOnce(final float delta) {
    final long startTime = System.currentTimeMillis();
    engine.update(delta);
    ++updates;
    return System.currentTimeMillis() - startTime;
  }

}
