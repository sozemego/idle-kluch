package com.soze.idlekluch.kingdom.entity;

import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Table;

@Embeddable
@Table(name = "storage_units")
public class StorageUnit {

  @EmbeddedId
  private EntityUUID buildingId;

  @EmbeddedId
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
