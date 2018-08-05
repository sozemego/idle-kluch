package com.soze.idlekluch.game.upgrade.service;

import com.soze.idlekluch.game.engine.components.BaseComponent;
import com.soze.idlekluch.game.message.UpgradeComponentMessage;

/**
 * Responsible for upgrading various {@link BaseComponent}.
 */
public interface UpgradeService {

  void upgradeComponent(String owner, UpgradeComponentMessage message);

  public enum UpgradeType {
    HARVESTER_SPEED
  }

}
