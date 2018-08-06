package com.soze.idlekluch.game.message;

import com.soze.idlekluch.game.upgrade.dto.Upgrade;
import com.soze.idlekluch.game.upgrade.service.UpgradeService.UpgradeType;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class UpgradesMessage extends OutgoingMessage {

  final Map<UpgradeType, Collection<Upgrade>> upgrades;

  public UpgradesMessage(final Map<UpgradeType, Collection<Upgrade>> upgrades) {
    super(OutgoingMessageType.UPGRADES);
    this.upgrades = Objects.requireNonNull(upgrades);
  }

  public Map<UpgradeType, Collection<Upgrade>> getUpgrades() {
    return upgrades;
  }
}
