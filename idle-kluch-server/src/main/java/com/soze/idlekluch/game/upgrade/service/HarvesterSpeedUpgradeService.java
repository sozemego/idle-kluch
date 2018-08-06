package com.soze.idlekluch.game.upgrade.service;

import com.soze.idlekluch.core.exception.EntityDoesNotExistException;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.components.OwnershipComponent;
import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceHarvesterComponent;
import com.soze.idlekluch.game.exception.GameException;
import com.soze.idlekluch.game.exception.NotEnoughIdleBucksException;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.service.KingdomService;
import com.soze.klecs.entity.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class HarvesterSpeedUpgradeService {

  private final GameEngine gameEngine;
  private final KingdomService kingdomService;
  private final UpgradeCostService upgradeCostService;
  private final List<Float> speedUpgrades = new ArrayList<>();

  @Autowired
  public HarvesterSpeedUpgradeService(final GameEngine gameEngine,
                                      final KingdomService kingdomService,
                                      final UpgradeCostService upgradeCostService) {
    this.gameEngine = Objects.requireNonNull(gameEngine);
    this.kingdomService = Objects.requireNonNull(kingdomService);
    this.upgradeCostService = Objects.requireNonNull(upgradeCostService);
  }

  @PostConstruct
  public void setup() {
    speedUpgrades.add(1.2f);
    speedUpgrades.add(1.4f);
    speedUpgrades.add(1.4f);
    speedUpgrades.add(1.6f);
    speedUpgrades.add(2f);
    speedUpgrades.add(2f);
    speedUpgrades.add(2f);
    speedUpgrades.add(2f);
  }

  public void upgradeHarvesterSpeed(final UUID messageId, final EntityUUID entityId) {
    Objects.requireNonNull(messageId);
    Objects.requireNonNull(entityId);

    final Entity entity = gameEngine
                            .getEntity(entityId)
                            .<EntityDoesNotExistException>orElseThrow(() -> {
                              throw new EntityDoesNotExistException(entityId + " does not exist", Entity.class);
                            });

    final ResourceHarvesterComponent harvesterComponent = entity.getComponent(ResourceHarvesterComponent.class);
    final int level = harvesterComponent.getSpeedLevel();
    final int cost = upgradeCostService
                       .getUpgradeCost(UpgradeService.UpgradeType.HARVESTER_SPEED, level)
                       .orElseThrow(() -> {
                         throw new GameException(messageId);
                       });

    final OwnershipComponent ownershipComponent = entity.getComponent(OwnershipComponent.class);
    final Kingdom kingdom = kingdomService.getKingdom(ownershipComponent.getOwnerId()).get();

    if (kingdom.getIdleBucks() < cost) {
      throw new NotEnoughIdleBucksException(messageId);
    }

    kingdom.setIdleBucks(kingdom.getIdleBucks() - cost);
    kingdomService.updateKingdom(kingdom);

    harvesterComponent.setUnitsPerMinute(harvesterComponent.getUnitsPerMinute() * getHarvestingSpeedChange(level));
    harvesterComponent.setSpeedLevel(level + 1);
  }

  private float getHarvestingSpeedChange(final int level) {
    return speedUpgrades.get(level - 1);
  }

}
