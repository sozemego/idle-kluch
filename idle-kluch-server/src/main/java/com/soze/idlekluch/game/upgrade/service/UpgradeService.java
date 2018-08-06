package com.soze.idlekluch.game.upgrade.service;

import com.soze.idlekluch.game.engine.components.BaseComponent;
import com.soze.idlekluch.game.message.UpgradeComponentMessage;
import com.soze.idlekluch.game.upgrade.dto.Upgrade;

import java.util.Collection;
import java.util.Map;

/**
 * Responsible for upgrading various {@link BaseComponent}.
 */
public interface UpgradeService {

  void upgradeComponent(String owner, UpgradeComponentMessage message);

  Map<UpgradeType, Collection<Upgrade>> getUpgrades();

  public enum UpgradeType {
    HARVESTER_SPEED
  }

}
