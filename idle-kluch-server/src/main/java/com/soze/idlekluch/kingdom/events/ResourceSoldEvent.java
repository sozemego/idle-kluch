package com.soze.idlekluch.kingdom.events;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.kingdom.entity.Resource;

import java.util.Objects;

public class ResourceSoldEvent {

  private final EntityUUID kingdomId;
  private final Resource resource;

  public ResourceSoldEvent(final EntityUUID kingdomId, final Resource resource) {
    this.kingdomId = Objects.requireNonNull(kingdomId);
    this.resource = Objects.requireNonNull(resource);
  }

  public EntityUUID getKingdomId() {
    return kingdomId;
  }

  public Resource getResource() {
    return resource;
  }
}
