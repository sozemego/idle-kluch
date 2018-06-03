package com.soze.idlekluch.kingdom.events;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import java.util.Objects;

public class KingdomAddedEvent {

  private final EntityUUID kingdomId;

  public KingdomAddedEvent(final EntityUUID kingdomId) {
    this.kingdomId = Objects.requireNonNull(kingdomId);
  }

  public EntityUUID getKingdomId() {
    return kingdomId;
  }
}
