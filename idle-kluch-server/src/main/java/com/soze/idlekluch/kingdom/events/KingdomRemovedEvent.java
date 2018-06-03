package com.soze.idlekluch.kingdom.events;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import java.util.Objects;

public class KingdomRemovedEvent {

  private final EntityUUID kingdomId;

  public KingdomRemovedEvent(final EntityUUID kingdomId) {
    this.kingdomId = Objects.requireNonNull(kingdomId);
  }

  public EntityUUID getKingdomId() {
    return kingdomId;
  }
}
