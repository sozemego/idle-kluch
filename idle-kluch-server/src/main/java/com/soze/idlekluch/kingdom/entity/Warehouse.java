package com.soze.idlekluch.kingdom.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "warehouses")
public class Warehouse extends Building {

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "storage_units")
  private List<StorageUnit> storageUnits;

  public Warehouse() {

  }

  public List<StorageUnit> getStorageUnits() {
    return storageUnits;
  }

  public void setStorageUnits(final List<StorageUnit> storageUnits) {
    this.storageUnits = storageUnits;
  }
}
