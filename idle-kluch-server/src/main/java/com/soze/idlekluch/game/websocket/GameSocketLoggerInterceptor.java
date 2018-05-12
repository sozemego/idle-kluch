package com.soze.idlekluch.game.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Service;

/**
 * This class is responsible for logging incoming WebSocket messages.
 */
@Service
public class GameSocketLoggerInterceptor extends ChannelInterceptorAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(GameSocketLoggerInterceptor.class);

  @Override
  public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
    final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    LOG.info("Session [{}]. Command [{}]", accessor.getSessionId(), accessor.getCommand());

    if(accessor.getCommand() == StompCommand.SUBSCRIBE) {
      LOG.info("Session [{}] is subscribing to [{}]", accessor.getSessionId(), accessor.getDestination());
    }

    if(accessor.getCommand() == StompCommand.SEND) {
      LOG.info("Session [{}] is sending to [{}]", accessor.getSessionId(), accessor.getDestination());
    }

    return message;
  }

}
