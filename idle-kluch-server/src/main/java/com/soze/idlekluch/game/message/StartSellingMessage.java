package com.soze.idlekluch.game.message;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.kingdom.entity.Resource;

import java.util.Objects;

public class StartSellingMessage extends OutgoingMessage {

  @JsonUnwrapped
  private final EntityUUID entityId;
  private final Resource resource;

  public StartSellingMessage(final EntityUUID entityId, final Resource resource) {
    super(OutgoingMessageType.START_SELLING);
    this.entityId = Objects.requireNonNull(entityId);
    this.resource = Objects.requireNonNull(resource);
  }

  public EntityUUID getEntityId() {
    return entityId;
  }

  public Resource getResource() {
    return resource;
  }
}
