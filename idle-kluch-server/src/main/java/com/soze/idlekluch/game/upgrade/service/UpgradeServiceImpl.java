package com.soze.idlekluch.game.upgrade.service;

import com.soze.idlekluch.core.aop.annotations.Profiled;
import com.soze.idlekluch.core.exception.EntityDoesNotExistException;
import com.soze.idlekluch.game.engine.components.OwnershipComponent;
import com.soze.idlekluch.game.exception.InvalidOwnerException;
import com.soze.idlekluch.game.message.UpgradeComponentMessage;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.game.upgrade.dto.Upgrade;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.service.KingdomService;
import com.soze.klecs.entity.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class UpgradeServiceImpl implements UpgradeService {

  private final HarvesterSpeedUpgradeService harvesterSpeedUpgradeService;
  private final KingdomService kingdomService;
  private final GameEngine gameEngine;
  private final UpgradeDataService upgradeDataService;

  @Autowired
  public UpgradeServiceImpl(final HarvesterSpeedUpgradeService harvesterSpeedUpgradeService,
                            final KingdomService kingdomService,
                            final GameEngine gameEngine,
                            final UpgradeDataService upgradeDataService) {
    this.harvesterSpeedUpgradeService = Objects.requireNonNull(harvesterSpeedUpgradeService);
    this.kingdomService = Objects.requireNonNull(kingdomService);
    this.gameEngine = Objects.requireNonNull(gameEngine);
    this.upgradeDataService = Objects.requireNonNull(upgradeDataService);
  }

  @Override
  @Profiled
  public void upgradeComponent(final String owner, final UpgradeComponentMessage message) {
    Objects.requireNonNull(owner);
    Objects.requireNonNull(message);

    final Entity entity = gameEngine
                            .getEntity(message.getEntityId())
                            .<EntityDoesNotExistException>orElseThrow(() -> {
                              throw new EntityDoesNotExistException(message.getEntityId() + " does not exist", Entity.class);
                            });

    final Kingdom kingdom = kingdomService
                              .getUsersKingdom(owner)
                              .orElse(new Kingdom());

    final OwnershipComponent ownershipComponent = entity.getComponent(OwnershipComponent.class);
    if (ownershipComponent != null && !ownershipComponent.getOwnerId().equals(kingdom.getKingdomId())) {
      throw new InvalidOwnerException(message.getMessageId(), owner + " is not the owner of " + message.getEntityId());
    }

    switch (message.getUpgradeType()) {
      case HARVESTER_SPEED:
        upgradeHarvesterSpeed(message);
        break;
      default:
        break;
    }
  }

  @Override
  public Map<UpgradeType, Collection<Upgrade>> getUpgrades() {
    return upgradeDataService.getUpgrades();
  }

  private void upgradeHarvesterSpeed(final UpgradeComponentMessage message) {
    harvesterSpeedUpgradeService.upgradeHarvesterSpeed(message.getMessageId(), message.getEntityId());
  }

}
