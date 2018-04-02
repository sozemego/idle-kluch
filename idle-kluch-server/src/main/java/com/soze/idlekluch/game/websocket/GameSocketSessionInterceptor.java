package com.soze.idlekluch.game.websocket;

import com.soze.idlekluch.game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GameSocketSessionInterceptor extends ChannelInterceptorAdapter {

  private final GameService gameService;

  @Autowired
  public GameSocketSessionInterceptor(final GameService gameService) {
    this.gameService = Objects.requireNonNull(gameService);
  }

  @Override
  public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
    final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if(StompCommand.CONNECT == accessor.getCommand()) {
      final String sessionId = accessor.getSessionId();
      final String username = accessor.getUser().getName();
      gameService.onConnect(sessionId, username);
    }

    if(StompCommand.DISCONNECT == accessor.getCommand()) {
      final String sessionId = accessor.getSessionId();
      gameService.onDisconnect(sessionId);
    }

    return message;
  }

}
