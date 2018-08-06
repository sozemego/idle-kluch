package com.soze.idlekluch.game.upgrade.dto;

import com.soze.idlekluch.game.upgrade.service.UpgradeService.UpgradeType;
import com.soze.klecs.entity.Entity;

import java.util.Objects;
import java.util.function.Consumer;

public class Upgrade {

  private final UpgradeType upgradeType;
  private final int level;
  private final int cost;
  private final Consumer<Entity> updater;

  public Upgrade(final UpgradeType upgradeType,
                 final int level,
                 final int cost,
                 final Consumer<Entity> updater) {
    this.upgradeType = Objects.requireNonNull(upgradeType);
    this.level = level;
    this.cost = cost;
    this.updater = Objects.requireNonNull(updater);
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

  public Consumer<Entity> getUpdater() {
    return updater;
  }
}
