package com.soze.idlekluch.game.service;

import org.springframework.context.annotation.Profile;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Profile("integration-test")
public class WebSocketMessagingServiceTest implements WebSocketMessagingService {

  private final List<Object[]> userMessages = new ArrayList<>();
  private final List<Object[]> broadcastMessages = new ArrayList<>();

  @Override
  public void sendToUser(final String username, final String destination, final Object message) {
    userMessages.add(new Object[]{username, destination, message});
  }

  @Override
  public void send(final String destination, final Object message) {
    broadcastMessages.add(new Object[]{destination, message});
  }

  @Override
  public void sendToSession(final String sessionId, final String destination, final Object message, final MessageHeaders headers) {

  }

  public List<Object[]> getUserMessages() {
    return userMessages;
  }

  public List<Object[]> getBroadcastMessages() {
    return broadcastMessages;
  }

  public List<Object[]> getBroadcastMessages(Class messageClazz) {
    return broadcastMessages
             .stream()
             .filter(o -> o[1].getClass().isAssignableFrom(messageClazz))
             .collect(Collectors.toList());
  }

  public void clearMessages() {
    userMessages.clear();
    broadcastMessages.clear();
  }

}
