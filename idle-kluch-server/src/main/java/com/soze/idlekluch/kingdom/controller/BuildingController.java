package com.soze.idlekluch.kingdom.controller;

import com.soze.idlekluch.kingdom.dto.BuildBuildingForm;
import com.soze.idlekluch.kingdom.dto.BuildingDefinitionDto;
import com.soze.idlekluch.kingdom.dto.BuildingDto;
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
    final List<BuildingDto> dtos = buildingService.getOwnBuildings(principal.getName());

    return ResponseEntity.ok(dtos);
  }

  @PostMapping(path = Routes.BUILDING_BUILD)
  public ResponseEntity buildBuilding(final Principal principal, @RequestBody final BuildBuildingForm form) {
    buildingService.buildBuilding(principal.getName(), form);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

}
