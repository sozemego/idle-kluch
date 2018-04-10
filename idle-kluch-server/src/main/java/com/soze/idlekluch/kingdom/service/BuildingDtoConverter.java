package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.kingdom.dto.BuildingDefinitionDto;
import com.soze.idlekluch.kingdom.dto.BuildingDto;
import com.soze.idlekluch.kingdom.dto.WarehouseDto;
import com.soze.idlekluch.kingdom.entity.Building;
import com.soze.idlekluch.kingdom.entity.Warehouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class BuildingDtoConverter {

  private final BuildingService buildingService;

  @Autowired
  public BuildingDtoConverter(final BuildingService buildingService) {
    this.buildingService = Objects.requireNonNull(buildingService);
  }

  public List<BuildingDto> convertBuildings(final List<Building> buildings) {
    Objects.requireNonNull(buildings);

    final List<BuildingDto> dtos = new ArrayList<>();

    for (final Building building : buildings) {
      dtos.add(convertBuilding(building));
    }

    return dtos;
  }

  public BuildingDto convertBuilding(final Building building) {
    switch (building.getBuildingType()) {
      case WAREHOUSE:
        return convertWarehouse((Warehouse) building);
    }
    return null;
  }

  public WarehouseDto convertWarehouse(final Warehouse warehouse) {
    Objects.requireNonNull(warehouse);

    final String asset = getAsset(warehouse);

    return new WarehouseDto(
      warehouse.getBuildingId().toString(),
      warehouse.getCreatedAt().toString(),
      warehouse.getName(),
      asset,
      warehouse.getX(),
      warehouse.getY(),
      warehouse.getStorageUnits()
    );
  }

  private String getAsset(final Building building) {
    final Map<String, BuildingDefinitionDto> constructableBuildings = buildingService.getAllConstructableBuildings();
    return constructableBuildings.get(building.getDefinitionId()).getAsset();
  }

}
