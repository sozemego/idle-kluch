package com.soze.idlekluch.game.upgrade.dto;

import com.soze.idlekluch.game.upgrade.service.UpgradeService.UpgradeType;

import java.util.Objects;

public class Upgrade {

  private final UpgradeType upgradeType;
  private final int level;
  private final int cost;

  public Upgrade(final UpgradeType upgradeType,
                 final int level,
                 final int cost) {
    this.upgradeType = Objects.requireNonNull(upgradeType);
    this.level = level;
    this.cost = cost;
  }

  public UpgradeType getUpgradeType() {
    return upgradeType;
  }

  public int getLevel() {
    return level;
  }

  public int getCost() {
    return cost;
  }

}
