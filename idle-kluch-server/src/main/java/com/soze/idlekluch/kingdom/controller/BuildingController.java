package com.soze.idlekluch.kingdom.controller;

import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.kingdom.dto.BuildingDefinitionDto;
import com.soze.idlekluch.kingdom.dto.BuildingDto;
import com.soze.idlekluch.kingdom.service.BuildingDtoConverter;
import com.soze.idlekluch.kingdom.entity.Building;
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
import java.util.List;
import java.util.Objects;

@Controller
public class BuildingController {

  private final BuildingService buildingService;
  private final BuildingDtoConverter buildingDtoConverter;

  @Autowired
  public BuildingController(final BuildingService buildingService,
                            final BuildingDtoConverter buildingDtoConverter) {
    this.buildingService = Objects.requireNonNull(buildingService);
    this.buildingDtoConverter = Objects.requireNonNull(buildingDtoConverter);
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
    final List<BuildingDto> dtos = buildingDtoConverter.convertBuildings(buildings);
    return ResponseEntity.ok(dtos);
  }

  @PostMapping(path = Routes.BUILDING_BUILD)
  public ResponseEntity buildBuilding(final Principal principal, @RequestBody final BuildBuildingForm form) {
    final Building building = buildingService.buildBuilding(principal.getName(), form);
    final BuildingDto buildingDto = buildingDtoConverter.convertBuilding(building);
    return ResponseEntity.status(HttpStatus.CREATED).body(buildingDto);
  }

}
