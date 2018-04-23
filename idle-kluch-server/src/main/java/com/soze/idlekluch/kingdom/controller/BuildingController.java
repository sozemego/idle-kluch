package com.soze.idlekluch.kingdom.controller;

import com.soze.idlekluch.kingdom.dto.BuildingDefinitionDto;
import com.soze.idlekluch.kingdom.service.BuildingService;
import com.soze.idlekluch.routes.Routes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;
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
    final Collection<BuildingDefinitionDto> buildingDefinitions = buildingService.getAllConstructableBuildings().values();

    return ResponseEntity.ok(buildingDefinitions);
  }

}
