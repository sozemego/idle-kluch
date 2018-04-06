package com.soze.idlekluch.game.service;

public interface WebSocketMessagingService {

  void sendToUser(final String username, final String destination, final Object message);

  void send(final String destination, final Object message);

}
