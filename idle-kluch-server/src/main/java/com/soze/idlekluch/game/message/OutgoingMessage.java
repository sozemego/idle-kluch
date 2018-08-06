package com.soze.idlekluch.game.message;

import java.util.Objects;

public abstract class OutgoingMessage {

  private final OutgoingMessageType type;

  public OutgoingMessage(final OutgoingMessageType type) {
    this.type = Objects.requireNonNull(type);
  }

  public OutgoingMessageType getType() {
    return type;
  }

  public enum OutgoingMessageType {
    WORLD_CHUNK, BUILDING_LIST, RESOURCE_LIST,
    ENTITY, ALREADY_CONNECTED, MESSAGE_REVERT, REMOVE_ENTITY, ENGINE_UPDATE, PAUSE_STATE,
    START_HARVESTING, START_SELLING, TRANSFER_RESOURCE, MODIFY_KINGDOM_BUCKS,
    COMPONENT_CHANGED, UPGRADES
  }

}
