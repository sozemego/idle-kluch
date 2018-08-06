package com.soze.idlekluch.game.upgrade.service;

import com.soze.idlekluch.core.exception.EntityDoesNotExistException;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.components.OwnershipComponent;
import com.soze.idlekluch.game.engine.components.ResourceSellerComponent;
import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceHarvesterComponent;
import com.soze.idlekluch.game.exception.GameException;
import com.soze.idlekluch.game.exception.NotEnoughIdleBucksException;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.game.service.WebSocketMessagingService;
import com.soze.idlekluch.game.upgrade.dto.Upgrade;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.service.KingdomService;
import com.soze.klecs.entity.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

import static com.soze.idlekluch.game.upgrade.service.UpgradeService.UpgradeType.SELLING_SPEED;

@Service
public class SellingSpeedUpgradeService {

  private final GameEngine gameEngine;
  private final KingdomService kingdomService;
  private final UpgradeDataService upgradeDataService;

  @Autowired
  public SellingSpeedUpgradeService(final GameEngine gameEngine,
                                      final KingdomService kingdomService,
                                      final UpgradeDataService upgradeDataService) {
    this.gameEngine = Objects.requireNonNull(gameEngine);
    this.kingdomService = Objects.requireNonNull(kingdomService);
    this.upgradeDataService = Objects.requireNonNull(upgradeDataService);
  }

  public void upgradeSellingSpeed(final UUID messageId, final EntityUUID entityId) {
    Objects.requireNonNull(messageId);
    Objects.requireNonNull(entityId);

    final Entity entity = gameEngine.getEntity(entityId).get();

    final ResourceSellerComponent seller = entity.getComponent(ResourceSellerComponent.class);
    final int level = seller.getSpeedLevel();
    final Upgrade upgrade = upgradeDataService.getUpgrade(SELLING_SPEED, level)
                              .<GameException>orElseThrow(() -> {
                                throw new GameException(messageId);
                              });

    final OwnershipComponent ownershipComponent = entity.getComponent(OwnershipComponent.class);
    final Kingdom kingdom = kingdomService.getKingdom(ownershipComponent.getOwnerId()).get();

    if (kingdom.getIdleBucks() < upgrade.getCost()) {
      throw new NotEnoughIdleBucksException(messageId);
    }

    kingdom.setIdleBucks(kingdom.getIdleBucks() - upgrade.getCost());
    kingdomService.updateKingdom(kingdom);
    final float nextSecondsPerUnit = (float) Math.floor(seller.getSecondsPerUnit() * (1 / (float) upgrade.getData()) * 100) / 100;
    seller.setSecondsPerUnit(nextSecondsPerUnit);
    seller.setSpeedLevel(level + 1);
  }

}
