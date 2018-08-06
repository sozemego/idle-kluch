package com.soze.idlekluch.game.upgrade.dto;

import com.soze.idlekluch.game.upgrade.service.UpgradeService.UpgradeType;

import java.util.Objects;

public class Upgrade {

  private final UpgradeType upgradeType;
  private final int level;
  private final int cost;
  private final Object data;

  public Upgrade(final UpgradeType upgradeType, final int level, final int cost, final Object data) {
    this.upgradeType = Objects.requireNonNull(upgradeType);
    this.level = level;
    this.cost = cost;
    this.data = Objects.requireNonNull(data);
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

  public Object getData() {
    return data;
  }
}
