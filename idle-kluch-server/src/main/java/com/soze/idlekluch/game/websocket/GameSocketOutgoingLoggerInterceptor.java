package com.soze.idlekluch.game.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Service;

/**
 * This object is logging outgoing WebSocket messages.
 */
@Service
public class GameSocketOutgoingLoggerInterceptor extends ChannelInterceptorAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(GameSocketOutgoingLoggerInterceptor.class);
  private static final Marker MESSAGE_MARKER = MarkerFactory.getMarker("MESSAGE");

  @Override
  public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
    final SimpMessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, SimpMessageHeaderAccessor.class);

    LOG.info(
      MESSAGE_MARKER,
      "Outgoing [{}] to [{}]. Message [{}]",
      accessor.getMessageType(),
      accessor.getDestination(),
      new String((byte[])message.getPayload())
    );

    return message;
  }

}
