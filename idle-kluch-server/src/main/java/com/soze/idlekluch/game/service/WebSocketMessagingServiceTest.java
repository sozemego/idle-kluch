package com.soze.idlekluch.game.service;

import org.springframework.context.annotation.Profile;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Profile("integration-test")
public class WebSocketMessagingServiceTest implements WebSocketMessagingService {

  private final List<Object[]> userMessages = new ArrayList<>();

  @Override
  public void sendToUser(final String username, final String destination, final Object message) {
    userMessages.add(new Object[]{username, destination, message});
  }

  @Override
  public void send(final String destination, final Object message) {

  }

  @Override
  public void sendToSession(final String sessionId, final String destination, final Object message, final MessageHeaders headers) {

  }

  public List<Object[]> getUserMessages() {
    return userMessages;
  }

  public void clearMessages() {
    userMessages.clear();
  }

}
