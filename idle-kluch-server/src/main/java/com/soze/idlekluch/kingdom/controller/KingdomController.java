package com.soze.idlekluch.kingdom.controller;

import com.soze.idlekluch.kingdom.dto.RegisterKingdomForm;
import com.soze.idlekluch.kingdom.service.KingdomService;
import com.soze.idlekluch.routes.Routes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Validation;
import javax.validation.Validator;
import java.security.Principal;
import java.util.Objects;

@Controller
public class KingdomController {

  private final KingdomService kingdomService;
  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

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

  @GetMapping(path = Routes.KINGDOM_CHECK_NAME_AVAILABLE + "/{name}")
  public ResponseEntity checkNameAvailable(@PathVariable("name") final String name) {
    final boolean available = kingdomService.isNameAvailable(name);
    return ResponseEntity.ok(available);
  }

  @DeleteMapping(path = Routes.KINGDOM_DELETE)
  public ResponseEntity deleteKingdom(final Principal principal) {
    this.kingdomService.removeKingdom(principal.getName());
    return ResponseEntity.ok().build();
  }

}
