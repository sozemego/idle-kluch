package com.soze.idlekluch.game.message;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.kingdom.entity.Resource;

import java.util.Objects;

public class TransferResourceMessage extends OutgoingMessage {

  private final EntityUUID from;
  private final EntityUUID to;
  private final Resource resource;

  public TransferResourceMessage(final EntityUUID from, final EntityUUID to, final Resource resource) {
    super(OutgoingMessageType.TRANSFER_RESOURCE);
    this.from = Objects.requireNonNull(from);
    this.to = Objects.requireNonNull(to);
    this.resource = Objects.requireNonNull(resource);
  }

  @JsonGetter("fromId")
  public String getFromString() {
    return from.toString();
  }

  @JsonGetter("toId")
  public String getToString() {
    return to.toString();
  }

  @JsonIgnore
  public EntityUUID getFrom() {
    return from;
  }

  @JsonIgnore
  public EntityUUID getTo() {
    return to;
  }

  public Resource getResource() {
    return resource;
  }
}
