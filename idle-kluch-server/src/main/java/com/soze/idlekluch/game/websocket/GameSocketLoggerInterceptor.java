package com.soze.idlekluch.game.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
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
  private static final Marker MESSAGE_MARKER = MarkerFactory.getMarker("MESSAGE");


  @Override
  public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
    final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    LOG.info(
      MESSAGE_MARKER,
      "Session [{}]. Command [{}]. Destination [{}]. Message [{}]",
      accessor.getSessionId(),
      accessor.getCommand(),
      accessor.getDestination(),
      new String((byte[]) message.getPayload())
    );

    return message;
  }

}
