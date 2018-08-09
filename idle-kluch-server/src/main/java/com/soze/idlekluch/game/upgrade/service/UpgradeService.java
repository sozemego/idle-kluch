package com.soze.idlekluch.game.upgrade.service;

import com.soze.idlekluch.core.event.AppStartedEvent;
import com.soze.idlekluch.game.engine.components.BaseComponent;
import com.soze.idlekluch.game.message.UpgradeComponentMessage;
import com.soze.idlekluch.game.upgrade.dto.Upgrade;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import java.util.Collection;
import java.util.Map;

/**
 * Responsible for upgrading various {@link BaseComponent}.
 */
public interface UpgradeService {

  void upgradeComponent(String owner, UpgradeComponentMessage message);

  Map<UpgradeType, Collection<Upgrade>> getUpgrades();

  @EventListener
  @Order(1)
  void handleAppStartedEvent(AppStartedEvent event);

  public enum UpgradeType {
    HARVESTER_SPEED, SELLING_SPEED, TRANSPORT_SPEED, NEXT_ROUTE_TIME
  }

}
