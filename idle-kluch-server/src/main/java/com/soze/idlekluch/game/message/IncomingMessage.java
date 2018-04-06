package com.soze.idlekluch.game.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Objects;

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

  private final IncomingMessageType incomingMessageType;

  public IncomingMessage(IncomingMessageType incomingMessageType) {
    this.incomingMessageType = Objects.requireNonNull(incomingMessageType);
  }

  public IncomingMessageType getIncomingMessageType() {
    return incomingMessageType;
  }

  public enum IncomingMessageType {
    BUILD_BUILDING
  }

}
