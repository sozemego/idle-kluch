package com.soze.idlekluch.game.upgrade.service;

import com.soze.idlekluch.core.exception.EntityDoesNotExistException;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.components.OwnershipComponent;
import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceHarvesterComponent;
import com.soze.idlekluch.game.exception.NotEnoughIdleBucksException;
import com.soze.idlekluch.game.service.EntityService;
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

  private final EntityService entityService;
  private final GameEngine gameEngine;
  private final KingdomService kingdomService;

  private final List<Integer> costs = new ArrayList<>();

  @Autowired
  public HarvesterSpeedUpgradeService(final EntityService entityService,
                                      final GameEngine gameEngine,
                                      final KingdomService kingdomService) {
    this.entityService = Objects.requireNonNull(entityService);
    this.gameEngine = Objects.requireNonNull(gameEngine);
    this.kingdomService = Objects.requireNonNull(kingdomService);
  }

  @PostConstruct
  public void setup() {
    costs.add(250);
    costs.add(500);
    costs.add(1000);
    costs.add(2000);
    costs.add(4000);
    costs.add(8000);
    costs.add(16000);
    costs.add(32000);
    costs.add(64000);
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
    final int cost = costs.get(level - 1);

    final OwnershipComponent ownershipComponent = entity.getComponent(OwnershipComponent.class);
    final Kingdom kingdom = kingdomService.getKingdom(ownershipComponent.getOwnerId()).get();

    if (kingdom.getIdleBucks() < cost) {
      throw new NotEnoughIdleBucksException(messageId);
    }

    kingdom.setIdleBucks(kingdom.getIdleBucks() - cost);
    kingdomService.updateKingdom(kingdom);

    harvesterComponent.setUnitsPerMinute(harvesterComponent.getUnitsPerMinute() * getHarvestingSpeedChange(level + 1));
    harvesterComponent.setSpeedLevel(level + 1);

  }

  private float getHarvestingSpeedChange(final int nextLevel) {
    return 2;
  }


}
