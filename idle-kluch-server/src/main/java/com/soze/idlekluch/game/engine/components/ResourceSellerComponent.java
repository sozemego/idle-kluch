package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.core.utils.MathUtils;
import com.soze.idlekluch.kingdom.entity.Resource;

import javax.persistence.Transient;

public class ResourceSellerComponent extends BaseComponent {

  private int secondsPerUnit;

  @Transient
  private float sellingProgress;

  @Transient
  private Resource resourceBeingSold;

  public ResourceSellerComponent() {
    super(ComponentType.SELLER);
  }

  public ResourceSellerComponent(final int secondsPerUnit) {
    this();
    this.secondsPerUnit = secondsPerUnit;
  }

  public int getSecondsPerUnit() {
    return secondsPerUnit;
  }

  public void setSecondsPerUnit(final int secondsPerUnit) {
    this.secondsPerUnit = secondsPerUnit;
  }

  public float getSellingProgress() {
    return sellingProgress;
  }

  public void setSellingProgress(final float sellingProgress) {
    this.sellingProgress = sellingProgress;
  }

  @Transient
  public boolean isFinished() {
    return MathUtils.equals(getSellingProgress(), 1f, 0.01f);
  }

  public Resource getResourceBeingSold() {
    return resourceBeingSold;
  }

  public void setResourceBeingSold(final Resource resourceBeingSold) {
    this.resourceBeingSold = resourceBeingSold;
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
