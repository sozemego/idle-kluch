package com.soze.idlekluch.game.service;

import com.soze.idlekluch.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Profile("!integration-test")
public class WebSocketMessagingServiceImpl implements WebSocketMessagingService {

  private static final Logger LOG = LoggerFactory.getLogger(WebSocketMessagingServiceImpl.class);

  private final SimpMessagingTemplate messagingTemplate;

  @Autowired
  public WebSocketMessagingServiceImpl(final SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = Objects.requireNonNull(messagingTemplate);
  }

  @Override
  public void sendToUser(final String username, final String destination, Object message) {
    Objects.requireNonNull(username);
    Objects.requireNonNull(destination);
    Objects.requireNonNull(message);

    LOG.info("[WEB_SOCKET_MESSAGING] Sending a message to user [{}] at [{}]", username, destination);
    messagingTemplate.convertAndSendToUser(username, "/game/outbound", convertMessage(message));
  }

  @Override
  public void send(final String destination, Object message) {
    Objects.requireNonNull(destination);
    Objects.requireNonNull(message);

    LOG.info("[WEB_SOCKET_MESSAGING] Sending a message to all users at [{}]", destination);
    messagingTemplate.convertAndSend("/game/outbound", convertMessage(message));
  }

  private Object convertMessage(final Object message) {
    if (!(message instanceof String)) {
      return JsonUtils.objectToJson(message);
    }
    return message;
  }
}
