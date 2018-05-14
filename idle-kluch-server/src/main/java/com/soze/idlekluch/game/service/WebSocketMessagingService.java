package com.soze.idlekluch.game.service;

import org.springframework.messaging.MessageHeaders;

public interface WebSocketMessagingService {

  void sendToUser(final String username, final String destination, final Object message);

  void send(final String destination, final Object message);

  void sendToSession(final String sessionId, final String destination, final Object message, final MessageHeaders headers);

}
