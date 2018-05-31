package com.soze.idlekluch.game.service;

import org.springframework.messaging.MessageHeaders;

public interface WebSocketMessagingService {

  void sendToUser(String username, String destination, Object message);

  void send(String destination, Object message);

  void sendToSession(String sessionId, String destination, Object message, MessageHeaders headers);

}
