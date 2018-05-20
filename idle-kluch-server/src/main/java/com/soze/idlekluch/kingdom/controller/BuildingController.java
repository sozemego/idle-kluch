package com.soze.idlekluch.kingdom.controller;

import com.soze.idlekluch.game.engine.EntityConverter;
import com.soze.idlekluch.game.message.EntityMessage;
import com.soze.idlekluch.kingdom.service.BuildingService;
import com.soze.idlekluch.routes.Routes;
import com.soze.klecs.entity.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class BuildingController {

  private final BuildingService buildingService;
  private final EntityConverter entityConverter;

  @Autowired
  public BuildingController(final BuildingService buildingService,
                            final EntityConverter entityConverter) {
    this.buildingService = Objects.requireNonNull(buildingService);
    this.entityConverter = Objects.requireNonNull(entityConverter);
  }

  @GetMapping(path = Routes.BUILDING_GET_ALL)
  public ResponseEntity getAllBuildings() {
    return ResponseEntity.status(400).build();
//    temporary endpoint
//    later a list of buildings will be filtered according to tech and player
//    final List<Entity> buildingDefinitions = buildingService.getAllConstructableBuildings();
//    final List<EntityMessage> entities = buildingDefinitions
//                                           .stream()
//                                           .map(entityConverter::toMessage)
//                                           .collect(Collectors.toList());
//
//    return ResponseEntity.ok(entities);
  }

}
