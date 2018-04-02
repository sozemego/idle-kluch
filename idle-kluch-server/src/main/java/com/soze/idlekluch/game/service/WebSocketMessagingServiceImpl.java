package com.soze.idlekluch.game.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class WebSocketMessagingServiceImpl implements WebSocketMessagingService {

  private static final Logger LOG = LoggerFactory.getLogger(WebSocketMessagingServiceImpl.class);

  @Autowired
  private SimpMessagingTemplate messageTemplate;

  @Override
  public void sendToUser(final String username, final String destination, final Object message) {
    Objects.requireNonNull(username);
    Objects.requireNonNull(destination);
    Objects.requireNonNull(message);
    System.out.println("WebSocketMessagingServiceImpl " + messageTemplate);
    LOG.info("Sending a message to user [{}] at [{}]", username, destination);
    messageTemplate.convertAndSendToUser(username, "/game/outbound", message);
  }
}
