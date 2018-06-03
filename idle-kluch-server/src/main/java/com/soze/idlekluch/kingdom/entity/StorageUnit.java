package com.soze.idlekluch.kingdom.entity;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class StorageUnit {

  @Embedded
  @AttributeOverride(name = "id", column = @Column(name = "resource_id", insertable = false, updatable = false))
  private EntityUUID resourceId;

  @Column(name = "amount")
  private int amount;

  @Column(name = "capacity")
  private int capacity;

  public StorageUnit() {

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

  public int getCapacity() {
    return capacity;
  }

  public void setCapacity(final int capacity) {
    this.capacity = capacity;
  }
}
