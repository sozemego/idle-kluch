package com.soze.idlekluch.game.message;

import java.util.Objects;

public class MessageRevert extends OutgoingMessage {

  private final String messageId;

  public MessageRevert(final String messageId) {
    super(OutgoingMessageType.MESSAGE_REVERT);
    this.messageId = Objects.requireNonNull(messageId);
  }

  public String getMessageId() {
    return messageId;
  }
}
