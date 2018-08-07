package com.soze.idlekluch.game.upgrade.repository;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.upgrade.service.UpgradeService.UpgradeType;

public interface UpgradeRepository {

  int getUpgradeLevel(final EntityUUID entityId, final UpgradeType upgradeType);
  void increaseUpgradeLevel(final EntityUUID entityUUID, final UpgradeType upgradeType);

}
