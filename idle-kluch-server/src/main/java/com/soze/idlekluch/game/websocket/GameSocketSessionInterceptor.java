package com.soze.idlekluch.game.websocket;

import com.soze.idlekluch.game.service.GameConnectionRegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * This object intercepts WebSocket messages and stores session/user information
 * in the {@link GameConnectionRegistryService}.
 */
@Service
public class GameSocketSessionInterceptor extends ChannelInterceptorAdapter {

  private final GameConnectionRegistryService gameConnectionRegistryService;

  @Autowired
  public GameSocketSessionInterceptor(GameConnectionRegistryService gameConnectionRegistryService) {
    this.gameConnectionRegistryService = Objects.requireNonNull(gameConnectionRegistryService);
  }

  @Override
  public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
    final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if(accessor == null) {
      System.out.println("Accessor is null in GameSocketSessionInterceptor");
      return message;
    }

    if(StompCommand.CONNECT == accessor.getCommand()) {
      final String sessionId = accessor.getSessionId();
      final String username = accessor.getUser().getName();
      gameConnectionRegistryService.onConnect(sessionId, username);
    }

    if(StompCommand.DISCONNECT == accessor.getCommand()) {
      final String sessionId = accessor.getSessionId();
      gameConnectionRegistryService.onDisconnect(sessionId);
    }

    return message;
  }

}
