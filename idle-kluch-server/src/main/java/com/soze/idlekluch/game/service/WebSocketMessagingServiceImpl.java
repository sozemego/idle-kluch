package com.soze.idlekluch.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class WebSocketMessagingServiceImpl implements WebSocketMessagingService {

  @Autowired
  private SimpMessageSendingOperations messageTemplate;

  @Override
  public void sendToUser(final String username, final Object message) {
    Objects.requireNonNull(username);
    Objects.requireNonNull(message);

//    messageTemplate.convertAndSend();
  }
}
