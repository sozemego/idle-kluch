package com.soze.idlekluch.game.message;

import com.soze.idlekluch.game.engine.components.resourcetransport.ResourceRoute;

import java.util.Objects;

public class TransferResourceMessage extends OutgoingMessage {

  private final ResourceRoute route;

  public TransferResourceMessage(final ResourceRoute route) {
    super(OutgoingMessageType.TRANSFER_RESOURCE);
    this.route = Objects.requireNonNull(route);
  }

  public ResourceRoute getRoute() {
    return route;
  }

}
