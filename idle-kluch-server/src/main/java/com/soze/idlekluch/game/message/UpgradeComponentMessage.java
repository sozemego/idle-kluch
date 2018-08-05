package com.soze.idlekluch.game.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.upgrade.service.UpgradeService.UpgradeType;

import java.util.Objects;
import java.util.UUID;

public class UpgradeComponentMessage extends IncomingMessage {

  private final EntityUUID entityId;
  private final UpgradeType upgradeType;

  @JsonCreator
  public UpgradeComponentMessage(@JsonProperty("messageId") final String messageId,
                                 @JsonProperty("buildingId") final String entityId,
                                 @JsonProperty("upgradeType") final String upgradeType) {
    super(UUID.fromString(messageId), IncomingMessageType.UPGRADE_COMPONENT);
    this.entityId = EntityUUID.fromString(Objects.requireNonNull(entityId));
    this.upgradeType = UpgradeType.valueOf(upgradeType);
  }

  public EntityUUID getEntityId() {
    return entityId;
  }

  public UpgradeType getUpgradeType() {
    return upgradeType;
  }
}
