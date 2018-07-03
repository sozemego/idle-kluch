package com.soze.idlekluch.game.engine.components.resourceharvester;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.soze.idlekluch.core.utils.MathUtils;

public class HarvestingProgress {

  private HarvestingState harvestingState;
  private float harvestingProgressPercent;

  public HarvestingProgress() {
    this.harvestingState = HarvestingState.WAITING;
    this.harvestingProgressPercent = 0f;
  }

  public HarvestingState getHarvestingState() {
    return harvestingState;
  }

  public void setHarvestingState(final HarvestingState harvestingState) {
    this.harvestingState = harvestingState;
  }

  public float getHarvestingProgressPercent() {
    return harvestingProgressPercent;
  }

  public void setHarvestingProgressPercent(final float harvestingProgressPercent) {
    this.harvestingProgressPercent = harvestingProgressPercent;
  }

  @JsonIgnore
  public boolean isFinished() {
    return MathUtils.equals(getHarvestingProgressPercent(), 1f, 0.01f);
  }

  public void start() {
    setHarvestingState(HarvestingState.HARVESTING);
    setHarvestingProgressPercent(0f);
  }

  public void stop() {
    setHarvestingState(HarvestingState.WAITING);
    setHarvestingProgressPercent(0f);
  }

  public enum HarvestingState {
    HARVESTING, WAITING
  }
}
