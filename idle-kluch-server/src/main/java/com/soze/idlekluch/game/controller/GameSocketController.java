package com.soze.idlekluch.game.controller;

import com.soze.idlekluch.SampleContextApplicationListener;
import com.soze.idlekluch.game.service.GameService;
import com.soze.idlekluch.game.service.WebSocketMessagingService;
import com.soze.idlekluch.routes.Routes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class GameSocketController {

  @Autowired
  private GameService gameService;

  @Autowired
  private WebSocketMessagingService webSocketMessagingService;

  @Autowired
  private SimpMessagingTemplate messageTemplate;

  @Autowired
  SampleContextApplicationListener sampleContextApplicationListener;

  @MessageMapping(Routes.GAME_INIT_MESSAGE)
  public void handleInitMessage(final Principal principal,
                                final Message message,
                                final SimpMessageHeaderAccessor headerAccessor) throws Exception {



    sampleContextApplicationListener.log();
    System.out.println("GameSocketController " + messageTemplate);
    webSocketMessagingService.sendToUser(principal.getName(), "/game/outbound", "MESSAGE");
    messageTemplate.convertAndSendToUser(principal.getName(), "/game/outbound", "MESSAGE");
    gameService.handleInitMessage(principal.getName());
  }

}
