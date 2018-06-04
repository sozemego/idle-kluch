package com.soze.idlekluch.game.controller;

import com.soze.idlekluch.game.exception.GameException;
import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.game.message.MessageRevert;
import com.soze.idlekluch.game.message.PauseToggleMessage;
import com.soze.idlekluch.game.service.GameConnectionRegistryService;
import com.soze.idlekluch.game.service.GameService;
import com.soze.idlekluch.core.routes.Routes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Objects;

@Controller
public class GameSocketController {

  private final GameService gameService;
  private final GameConnectionRegistryService gameConnectionRegistryService;

  @Autowired
  public GameSocketController(final GameService gameService, final GameConnectionRegistryService gameConnectionRegistryService) {
    this.gameService = Objects.requireNonNull(gameService);
    this.gameConnectionRegistryService = Objects.requireNonNull(gameConnectionRegistryService);
  }

  @MessageMapping(Routes.GAME_INIT_MESSAGE)
  public void handleInitMessage(final Principal principal, final SimpMessageHeaderAccessor headerAccessor) {
    final String sessionId = headerAccessor.getSessionId();
    if (gameConnectionRegistryService.isDuplicate(sessionId)) {
      gameService.handleDuplicateSession(sessionId);
      return;
    }

    gameService.handleInitMessage(principal.getName());
  }

  @MessageMapping(Routes.BUILD_BUILDING_MESSAGE)
  public void handleBuildBuildingMessage(final Principal principal, final BuildBuildingForm message) {
    gameService.handleBuildBuildingMessage(principal.getName(), message);
  }

  @MessageMapping(Routes.PAUSE_TOGGLE_MESSAGE)
  public void handleBuildBuildingMessage(final Principal principal) {
    gameService.handlePauseToggle();
  }

  @MessageExceptionHandler(GameException.class)
  @SendToUser(Routes.GAME_OUTBOUND)
  public MessageRevert handleGameException(final GameException exception) {
    return new MessageRevert(exception.getMessageId().toString());
  }

}
