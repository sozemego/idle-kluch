package com.soze.idlekluch.game.message;

import com.soze.idlekluch.kingdom.entity.Resource;

import java.util.List;
import java.util.Objects;

public class ResourceListMessage extends OutgoingMessage {

  private final List<Resource> resources;

  public ResourceListMessage(final List<Resource> resources) {
    super(OutgoingMessageType.RESOURCE_LIST);
    this.resources = Objects.requireNonNull(resources);
  }

  public List<Resource> getResources() {
    return resources;
  }

  @Override
  public OutgoingMessageType getType() {
    return OutgoingMessageType.RESOURCE_LIST;
  }
}
