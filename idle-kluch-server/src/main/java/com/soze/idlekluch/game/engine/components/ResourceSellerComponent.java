package com.soze.idlekluch.game.engine.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.soze.idlekluch.core.utils.MathUtils;
import com.soze.idlekluch.kingdom.entity.Resource;

import javax.persistence.Column;
import javax.persistence.Transient;

public class ResourceSellerComponent extends BaseComponent {

  private float secondsPerUnit;

  private float sellingProgress;

  private Resource resourceBeingSold;

  private int speedLevel = 1;

  public ResourceSellerComponent() {
    super(ComponentType.SELLER);
  }

  public ResourceSellerComponent(final float secondsPerUnit) {
    this();
    this.secondsPerUnit = secondsPerUnit;
  }

  public float getSecondsPerUnit() {
    return secondsPerUnit;
  }

  public void setSecondsPerUnit(final float secondsPerUnit) {
    this.secondsPerUnit = secondsPerUnit;
  }

  @Transient
  public float getSellingProgress() {
    return sellingProgress;
  }

  public void setSellingProgress(final float sellingProgress) {
    this.sellingProgress = sellingProgress;
  }

  @JsonIgnore
  public boolean isFinished() {
    return MathUtils.equals(getSellingProgress(), 1f, 0.01f);
  }

  @Transient
  public Resource getResourceBeingSold() {
    return resourceBeingSold;
  }

  public void setResourceBeingSold(final Resource resourceBeingSold) {
    this.resourceBeingSold = resourceBeingSold;
  }

  @Transient
  public int getSpeedLevel() {
    return speedLevel;
  }

  public void setSpeedLevel(final int speedLevel) {
    this.speedLevel = speedLevel;
  }

  public void startSelling(final Resource resourceBeingSold) {
    setResourceBeingSold(resourceBeingSold);
    setSellingProgress(0f);
  }

  public void stopSelling() {
    setResourceBeingSold(null);
    setSellingProgress(0f);
  }

  @Override
  public BaseComponent copy() {
    return new ResourceSellerComponent(
      getSecondsPerUnit()
    );
  }

}
