package com.soze.idlekluch.kingdom.controller;

import com.soze.idlekluch.core.exception.EntityDoesNotExistException;
import com.soze.idlekluch.kingdom.dto.KingdomDto;
import com.soze.idlekluch.kingdom.dto.RegisterKingdomForm;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.exception.UserDoesNotHaveKingdomException;
import com.soze.idlekluch.kingdom.service.KingdomService;
import com.soze.idlekluch.core.routes.Routes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Objects;

@Controller
public class KingdomController {

  private static final Logger LOG = LoggerFactory.getLogger(KingdomController.class);

  private final KingdomService kingdomService;

  @Autowired
  public KingdomController(final KingdomService kingdomService) {
    this.kingdomService = Objects.requireNonNull(kingdomService);
  }

  @PostMapping(path = Routes.KINGDOM_CREATE)
  public ResponseEntity createKingdom(final Principal principal,
                                      @RequestBody final RegisterKingdomForm form) {

    final String username = principal.getName();
    this.kingdomService.addKingdom(username, form);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping(path = Routes.KINGDOM_OWN)
  public ResponseEntity getOwnKingdom(final Principal principal) {
    final Kingdom kingdom = kingdomService
                              .getUsersKingdom(principal.getName())
                              .orElseThrow(() -> new UserDoesNotHaveKingdomException(principal.getName()));

    final KingdomDto dto = convertKingdomDto(kingdom);
    return ResponseEntity.ok(dto);
  }

  @GetMapping(path = Routes.KINGDOM_GET + "/{name}")
  public ResponseEntity getKingdom(@PathVariable("name") final String name) {

    final Kingdom kingdom = kingdomService
                              .getKingdom(name)
                              .orElseThrow(() -> new EntityDoesNotExistException("Kingdom named " + name + " does not exist.", Kingdom.class));

    LOG.info("Found kingdom with name [{}], returning", name);

    final KingdomDto dto = convertKingdomDto(kingdom);
    return ResponseEntity.ok(dto);
  }

  @DeleteMapping(path = Routes.KINGDOM_DELETE)
  public ResponseEntity deleteKingdom(final Principal principal) {
    this.kingdomService.removeKingdom(principal.getName());
    return ResponseEntity.ok().build();
  }

  private KingdomDto convertKingdomDto(final Kingdom kingdom) {
    return new KingdomDto(
      kingdom.getName(),
      kingdom.getOwner().getUsername(),
      kingdom.getCreatedAt().toString(),
      kingdom.getStartingPoint(),
      kingdom.getIdleBucks()
    );
  }

}
