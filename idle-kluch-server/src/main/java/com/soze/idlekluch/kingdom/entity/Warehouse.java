package com.soze.idlekluch.kingdom.entity;

import com.soze.idlekluch.kingdom.dto.BuildingDto.BuildingType;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "warehouses")
public class Warehouse extends Building {

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "storage_units", joinColumns = @JoinColumn(name = "building_id"))
  private List<StorageUnit> storageUnits;

  public Warehouse() {
    setBuildingType(BuildingType.WAREHOUSE);
  }

  public List<StorageUnit> getStorageUnits() {
    return storageUnits;
  }

  public void setStorageUnits(final List<StorageUnit> storageUnits) {
    this.storageUnits = storageUnits;
  }
}
