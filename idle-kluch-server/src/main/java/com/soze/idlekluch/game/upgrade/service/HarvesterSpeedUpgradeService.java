package com.soze.idlekluch.game.upgrade.service;

import com.soze.idlekluch.core.exception.EntityDoesNotExistException;
import com.soze.idlekluch.core.routes.Routes;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.components.BaseComponent;
import com.soze.idlekluch.game.engine.components.OwnershipComponent;
import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceHarvesterComponent;
import com.soze.idlekluch.game.exception.GameException;
import com.soze.idlekluch.game.exception.NotEnoughIdleBucksException;
import com.soze.idlekluch.game.message.ComponentChangedMessage;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.game.service.WebSocketMessagingService;
import com.soze.idlekluch.game.upgrade.dto.Upgrade;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.service.KingdomService;
import com.soze.klecs.entity.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.soze.idlekluch.game.engine.components.BaseComponent.ComponentType.*;
import static com.soze.idlekluch.game.upgrade.service.UpgradeService.UpgradeType.*;

@Service
public class HarvesterSpeedUpgradeService {

  private final GameEngine gameEngine;
  private final KingdomService kingdomService;
  private final UpgradeDataService upgradeDataService;
  private final WebSocketMessagingService webSocketMessagingService;

  @Autowired
  public HarvesterSpeedUpgradeService(final GameEngine gameEngine,
                                      final KingdomService kingdomService,
                                      final UpgradeDataService upgradeDataService,
                                      final WebSocketMessagingService webSocketMessagingService) {
    this.gameEngine = Objects.requireNonNull(gameEngine);
    this.kingdomService = Objects.requireNonNull(kingdomService);
    this.upgradeDataService = Objects.requireNonNull(upgradeDataService);
    this.webSocketMessagingService = Objects.requireNonNull(webSocketMessagingService);
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
    final Upgrade upgrade = upgradeDataService.getUpgrade(HARVESTER_SPEED, level)
                              .orElseThrow(() -> {
                                throw new GameException(messageId);
                              });

    final OwnershipComponent ownershipComponent = entity.getComponent(OwnershipComponent.class);
    final Kingdom kingdom = kingdomService.getKingdom(ownershipComponent.getOwnerId()).get();

    if (kingdom.getIdleBucks() < upgrade.getCost()) {
      throw new NotEnoughIdleBucksException(messageId);
    }

    kingdom.setIdleBucks(kingdom.getIdleBucks() - upgrade.getCost());
    kingdomService.updateKingdom(kingdom);

    harvesterComponent.setUnitsPerMinute(harvesterComponent.getUnitsPerMinute() * (float) upgrade.getData());
    harvesterComponent.setSpeedLevel(level + 1);
    webSocketMessagingService.send(
      Routes.GAME_OUTBOUND,
      new ComponentChangedMessage(
        entityId.toString(),
        RESOURCE_HARVESTER,
        "unitsPerMinute",
        harvesterComponent.getUnitsPerMinute()
      )
    );
  }

}
