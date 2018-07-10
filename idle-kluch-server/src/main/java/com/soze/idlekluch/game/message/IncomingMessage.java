package com.soze.idlekluch.game.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Objects;
import java.util.UUID;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "type",
  visible = true
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = BuildBuildingForm.class, name = "BUILD_BUILDING"),
  @JsonSubTypes.Type(value = PauseToggleMessage.class, name = "PAUSE_TOGGLE"),
  @JsonSubTypes.Type(value = AttachResourceSourceForm.class, name = "ATTACH_RESOURCE_SOURCE"),
})
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
    BUILD_BUILDING, PAUSE_TOGGLE, ATTACH_RESOURCE_SOURCE
  }

}
