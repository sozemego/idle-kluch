package com.soze.idlekluch.game.service;

public interface WebSocketMessagingService {

  public void sendToUser(final String username, final String destination, final Object message);

}
