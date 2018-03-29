package com.soze.idlekluch.kingdom.controller;

import com.soze.idlekluch.kingdom.dto.BuildBuildingForm;
import com.soze.idlekluch.kingdom.dto.BuildingDefinitionDto;
import com.soze.idlekluch.kingdom.dto.BuildingDto;
import com.soze.idlekluch.kingdom.dto.WarehouseDto;
import com.soze.idlekluch.kingdom.entity.Building;
import com.soze.idlekluch.kingdom.entity.Warehouse;
import com.soze.idlekluch.kingdom.service.BuildingService;
import com.soze.idlekluch.routes.Routes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class BuildingController {

  private final BuildingService buildingService;

  @Autowired
  public BuildingController(final BuildingService buildingService) {
    this.buildingService = Objects.requireNonNull(buildingService);
  }

  @GetMapping(path = Routes.BUILDING_GET_ALL)
  public ResponseEntity getAllBuildings() {
    //temporary endpoint
    //later a list of buildings will be filtered according to tech and player
    final List<BuildingDefinitionDto> buildingDefinitions = buildingService.getAllConstructableBuildings();

    return ResponseEntity.ok(buildingDefinitions);
  }

  @GetMapping(path = Routes.BUILDING_OWN)
  public ResponseEntity getOwnBuildings(final Principal principal) {
    final List<Building> buildings = buildingService.getOwnBuildings(principal.getName());
    final List<BuildingDto> dtos = convertBuildings(buildings);
    return ResponseEntity.ok(dtos);
  }

  @PostMapping(path = Routes.BUILDING_BUILD)
  public ResponseEntity buildBuilding(final Principal principal, @RequestBody final BuildBuildingForm form) {
    buildingService.buildBuilding(principal.getName(), form);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  private List<BuildingDto> convertBuildings(final List<Building> buildings) {
    Objects.requireNonNull(buildings);

    final List<BuildingDto> dtos = new ArrayList<>();

    for (final Building building : buildings) {
      switch (building.getBuildingType()) {
        case WAREHOUSE:
          dtos.add(convertWarehouse((Warehouse) building));
          break;
      }
    }

    return dtos;
  }

  private WarehouseDto convertWarehouse(final Warehouse warehouse) {
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
