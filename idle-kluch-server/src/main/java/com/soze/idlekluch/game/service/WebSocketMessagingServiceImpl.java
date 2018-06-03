package com.soze.idlekluch.game.service;

import com.soze.idlekluch.core.aop.annotations.Profiled;
import com.soze.idlekluch.core.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Profile("!integration-test")
public class WebSocketMessagingServiceImpl implements WebSocketMessagingService {

  private final SimpMessagingTemplate messagingTemplate;

  @Autowired
  public WebSocketMessagingServiceImpl(final SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = Objects.requireNonNull(messagingTemplate);
  }

  @Override
  @Profiled
  public void sendToUser(final String username, final String destination, Object message) {
    Objects.requireNonNull(username);
    Objects.requireNonNull(destination);
    Objects.requireNonNull(message);

    messagingTemplate.convertAndSendToUser(username, destination, convertMessage(message));
  }

  @Override
  @Profiled
  public void send(final String destination, final Object message) {
    Objects.requireNonNull(destination);
    Objects.requireNonNull(message);

    messagingTemplate.convertAndSend(destination, convertMessage(message));
  }

  @Override
  @Profiled
  public void sendToSession(final String sessionId, final String destination, final Object message, final MessageHeaders headers) {
    Objects.requireNonNull(sessionId);
    Objects.requireNonNull(destination);
    Objects.requireNonNull(message);
    Objects.requireNonNull(headers);

    messagingTemplate.convertAndSendToUser(sessionId, destination, convertMessage(message), headers);
  }

  private Object convertMessage(final Object message) {
    if (!(message instanceof String)) {
      return JsonUtils.objectToJson(message);
    }
    return message;
  }
}
