package com.soze.idlekluch.game.upgrade.dto;

import com.soze.idlekluch.game.upgrade.service.UpgradeService.UpgradeType;
import com.soze.klecs.entity.Entity;

import java.util.Objects;
import java.util.function.BiConsumer;

public class Upgrade {

  private final UpgradeType upgradeType;
  private final int level;
  private final int cost;
  private final Object data;
  private final BiConsumer<Entity, Object> updater;

  public Upgrade(final UpgradeType upgradeType,
                 final int level,
                 final int cost,
                 final Object data,
                 final BiConsumer<Entity, Object> updater) {
    this.upgradeType = Objects.requireNonNull(upgradeType);
    this.level = level;
    this.cost = cost;
    this.data = Objects.requireNonNull(data);
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

  public Object getData() {
    return data;
  }

  public void upgrade(final Entity entity) {
    updater.accept(entity, data);
  }

}
