package com.soze.idlekluch.game.message;

import com.fasterxml.jackson.annotation.*;

import java.util.Objects;
import java.util.UUID;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "type",
  visible = true
)
@JsonSubTypes(
  @JsonSubTypes.Type(value = BuildBuildingForm.class, name = "BUILD_BUILDING")
)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class IncomingMessage {

  private final UUID messageId;
  private final IncomingMessageType incomingMessageType;

  public IncomingMessage(final UUID messageId, final IncomingMessageType incomingMessageType) {
    this.messageId = Objects.requireNonNull(messageId);
    this.incomingMessageType = Objects.requireNonNull(incomingMessageType);
  }

  public UUID getMessageId() {
    return messageId;
  }

  public IncomingMessageType getIncomingMessageType() {
    return incomingMessageType;
  }

  public enum IncomingMessageType {
    BUILD_BUILDING
  }

}
