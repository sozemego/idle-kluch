package com.soze.idlekluch.game.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class PauseToggleMessage extends IncomingMessage {

  @JsonCreator
  public PauseToggleMessage(@JsonProperty("messageId") final String messageId) {
    super(UUID.fromString(messageId), IncomingMessageType.PAUSE_TOGGLE);
  }

}
