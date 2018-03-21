package com.soze.idlekluch.kingdom.controller;

import com.soze.idlekluch.kingdom.dto.KingdomDto;
import com.soze.idlekluch.kingdom.dto.RegisterKingdomForm;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.exception.InvalidRegisterKingdomException;
import com.soze.idlekluch.kingdom.service.KingdomService;
import com.soze.idlekluch.routes.Routes;
import com.soze.idlekluch.utils.ExceptionUtils;
import com.soze.idlekluch.utils.http.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Objects;
import java.util.Optional;

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
                                      @Valid @RequestBody final RegisterKingdomForm form,
                                      final BindingResult bindingResult) {

    for (final FieldError error: bindingResult.getFieldErrors()) {
      LOG.info("Kingdom creation by user [{}] rejected because [{}]", error.getDefaultMessage());
      throw new InvalidRegisterKingdomException(error.getField(), error.getDefaultMessage());
    }

    final String username = principal.getName();
    this.kingdomService.addKingdom(username, form);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping(path = Routes.KINGDOM_OWN)
  public ResponseEntity getOwnKingdom(final Principal principal) {
    final Optional<Kingdom> kingdomOptional = kingdomService.getUsersKingdom(principal.getName());

    if(!kingdomOptional.isPresent()) {
      final ErrorResponse errorResponse = new ErrorResponse(404, "Kingdom not found");
      return ExceptionUtils.convertErrorResponse(errorResponse);
    }

    final KingdomDto dto = convertKingdomDto(kingdomOptional.get());
    return ResponseEntity.ok(dto);
  }

  @GetMapping(path = Routes.KINGDOM_GET + "/{name}")
  public ResponseEntity getKingdom(@PathVariable("name") final String name) {
    final Optional<Kingdom> kingdomOptional = kingdomService.getKingdom(name);

    if(kingdomOptional.isPresent()) {
      LOG.info("Found kingdom with name [{}], returning", name);
      final Kingdom kingdom = kingdomOptional.get();
      final KingdomDto dto = convertKingdomDto(kingdom);
      return ResponseEntity.ok(dto);
    }

    LOG.info("Did not find kingdom with name [{}]", name);
    final ErrorResponse errorResponse = new ErrorResponse(404, "Kingdom not found");
    return ExceptionUtils.convertErrorResponse(errorResponse);
  }

  @DeleteMapping(path = Routes.KINGDOM_DELETE)
  public ResponseEntity deleteKingdom(final Principal principal) {
    this.kingdomService.removeKingdom(principal.getName());
    return ResponseEntity.ok().build();
  }

  private KingdomDto convertKingdomDto(final Kingdom kingdom) {
    return new KingdomDto(kingdom.getName(), kingdom.getOwner().getUsername(), kingdom.getCreatedAt().toString());
  }

}
