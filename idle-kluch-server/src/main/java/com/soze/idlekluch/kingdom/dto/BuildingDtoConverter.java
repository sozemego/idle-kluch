package com.soze.idlekluch.kingdom.dto;

import com.soze.idlekluch.kingdom.entity.Building;
import com.soze.idlekluch.kingdom.entity.Warehouse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BuildingDtoConverter {

  public static List<BuildingDto> convertBuildings(final List<Building> buildings) {
    Objects.requireNonNull(buildings);

    final List<BuildingDto> dtos = new ArrayList<>();

    for (final Building building : buildings) {
      dtos.add(convertBuilding(building));
    }

    return dtos;
  }

  public static BuildingDto convertBuilding(final Building building) {
    switch (building.getBuildingType()) {
      case WAREHOUSE:
        return convertWarehouse((Warehouse) building);
    }
    return null;
  }

  public static WarehouseDto convertWarehouse(final Warehouse warehouse) {
    Objects.requireNonNull(warehouse);

    return new WarehouseDto(
      warehouse.getBuildingId().toString(),
      warehouse.getCreatedAt().toString(),
      warehouse.getName(),
      warehouse.getX(),
      warehouse.getY(),
      warehouse.getStorageUnits()
    );
  }


}
