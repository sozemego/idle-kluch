package com.soze.idlekluch.game.engine.components;

import javax.persistence.Transient;

public class SellerComponent extends BaseComponent {

  private int secondsPerUnit;

  @Transient
  private float sellingProgress;

  public SellerComponent() {
    super(ComponentType.SELLER);
  }

  public SellerComponent(final int secondsPerUnit) {
    this();
    this.secondsPerUnit = secondsPerUnit;
  }

  public int getSecondsPerUnit() {
    return secondsPerUnit;
  }

  public void setSecondsPerUnit(final int secondsPerUnit) {
    this.secondsPerUnit = secondsPerUnit;
  }

  @Override
  public BaseComponent copy() {
    return new SellerComponent(
      getSecondsPerUnit()
    );
  }
}
