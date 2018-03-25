package com.soze.idlekluch.kingdom.entity;

import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.*;

@Embeddable
public class StorageUnit {

  @Embedded
  @AttributeOverride(name = "id", column = @Column(name = "building_id"))
  private EntityUUID buildingId;

  @Embedded
  @AttributeOverride(name = "id", column = @Column(name = "resource_id"))
  private EntityUUID resourceId;

  @Column(name = "amount")
  private int amount;

  @Column(name = "max_amount")
  private int maxAmount;

  public StorageUnit() {

  }

  public EntityUUID getBuildingId() {
    return buildingId;
  }

  public void setBuildingId(final EntityUUID buildingId) {
    this.buildingId = buildingId;
  }

  public EntityUUID getResourceId() {
    return resourceId;
  }

  public void setResourceId(final EntityUUID resourceId) {
    this.resourceId = resourceId;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(final int amount) {
    this.amount = amount;
  }

  public int getMaxAmount() {
    return maxAmount;
  }

  public void setMaxAmount(final int maxAmount) {
    this.maxAmount = maxAmount;
  }
}
