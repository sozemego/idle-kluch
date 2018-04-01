package com.soze.idlekluch.game.controller;

import com.soze.idlekluch.routes.Routes;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class GameSocketController {

  @MessageMapping(Routes.GAME_INBOUND)
  public void handleMessage(Principal principal,
                            SimpMessageHeaderAccessor headerAccessor) throws Exception {
    System.out.println(principal);
  }

}
