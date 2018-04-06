package com.soze.idlekluch.game.controller;

import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.game.service.GameService;
import com.soze.idlekluch.routes.Routes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class GameSocketController {

  @Autowired
  private GameService gameService;

  @MessageMapping(Routes.GAME_INIT_MESSAGE)
  public void handleInitMessage(final Principal principal,
                                final Message message,
                                final SimpMessageHeaderAccessor headerAccessor) throws Exception {

    gameService.handleInitMessage(principal.getName());
  }

  @MessageMapping(Routes.BUILD_BUILDING_MESSAGE)
  public void handleBuildBuildingMessage(final Principal principal,
                                         final BuildBuildingForm message) throws Exception {

    gameService.handleBuildBuildingMessage(principal.getName(), message);
  }

}
