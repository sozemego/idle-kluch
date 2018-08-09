package com.soze.idlekluch.game.upgrade.service;

import com.soze.idlekluch.core.aop.annotations.Profiled;
import com.soze.idlekluch.core.event.AppStartedEvent;
import com.soze.idlekluch.core.exception.EntityDoesNotExistException;
import com.soze.idlekluch.game.engine.EntityUtils;
import com.soze.idlekluch.game.engine.components.OwnershipComponent;
import com.soze.idlekluch.game.engine.components.ResourceSellerComponent;
import com.soze.idlekluch.game.engine.components.ResourceStorageComponent;
import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceHarvesterComponent;
import com.soze.idlekluch.game.engine.nodes.Nodes;
import com.soze.idlekluch.game.exception.GameException;
import com.soze.idlekluch.game.exception.InvalidOwnerException;
import com.soze.idlekluch.game.exception.NotEnoughIdleBucksException;
import com.soze.idlekluch.game.message.UpgradeComponentMessage;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.game.upgrade.dto.Upgrade;
import com.soze.idlekluch.game.upgrade.repository.UpgradeRepository;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.service.KingdomService;
import com.soze.klecs.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@Service
public class UpgradeServiceImpl implements UpgradeService {

  private static final Logger LOG = LoggerFactory.getLogger(UpgradeServiceImpl.class);

  private final KingdomService kingdomService;
  private final GameEngine gameEngine;
  private final UpgradeDataService upgradeDataService;
  private final UpgradeRepository upgradeRepository;

  @Autowired
  public UpgradeServiceImpl(final KingdomService kingdomService,
                            final GameEngine gameEngine,
                            final UpgradeDataService upgradeDataService,
                            final UpgradeRepository upgradeRepository) {
    this.kingdomService = Objects.requireNonNull(kingdomService);
    this.gameEngine = Objects.requireNonNull(gameEngine);
    this.upgradeDataService = Objects.requireNonNull(upgradeDataService);
    this.upgradeRepository = Objects.requireNonNull(upgradeRepository);
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

    final Upgrade upgrade = getUpgrade(message);
    if (kingdom.getIdleBucks() < upgrade.getCost()) {
      throw new NotEnoughIdleBucksException(message.getMessageId());
    }

    upgrade.upgrade(entity);
    upgradeRepository.increaseUpgradeLevel(message.getEntityId(), message.getUpgradeType());

    kingdom.setIdleBucks(kingdom.getIdleBucks() - upgrade.getCost());
    kingdomService.updateKingdom(kingdom);
  }

  @Override
  public Map<UpgradeType, Collection<Upgrade>> getUpgrades() {
    return upgradeDataService.getUpgrades();
  }

  @Override
  public void handleAppStartedEvent(final AppStartedEvent event) {
    updateUpgradeLevels();
  }

  private Upgrade getUpgrade(final UpgradeComponentMessage message) {
    final int currentLevel = upgradeRepository.getUpgradeLevel(message.getEntityId(), message.getUpgradeType());
    return upgradeDataService
             .getUpgrade(message.getUpgradeType(), currentLevel)
             .<GameException>orElseThrow(() -> {
               throw new GameException(message.getMessageId());
             });
  }

  private void updateUpgradeLevels() {
    LOG.info("Updating upgrade levels");
    gameEngine
      .getEntitiesByNode(Nodes.HARVESTER)
      .forEach(entity -> {
        final ResourceHarvesterComponent harvesterComponent = entity.getComponent(ResourceHarvesterComponent.class);
        harvesterComponent.setSpeedLevel(upgradeRepository.getUpgradeLevel(EntityUtils.getId(entity), UpgradeType.HARVESTER_SPEED));
      });

    gameEngine
      .getEntitiesByNode(Nodes.SELLER)
      .forEach(entity -> {
        final ResourceSellerComponent sellerComponent = entity.getComponent(ResourceSellerComponent.class);
        sellerComponent.setSpeedLevel(upgradeRepository.getUpgradeLevel(EntityUtils.getId(entity), UpgradeType.SELLING_SPEED));
      });

    gameEngine
      .getEntitiesByNode(Nodes.STORAGE)
      .forEach(entity -> {
        final ResourceStorageComponent storage = entity.getComponent(ResourceStorageComponent.class);
        storage.setTransportSpeedLevel(upgradeRepository.getUpgradeLevel(EntityUtils.getId(entity), UpgradeType.TRANSPORT_SPEED));
      });

    gameEngine
      .getEntitiesByNode(Nodes.STORAGE)
      .forEach(entity -> {
        final ResourceStorageComponent storage = entity.getComponent(ResourceStorageComponent.class);
        storage.setNextRouteTimeLevel(upgradeRepository.getUpgradeLevel(EntityUtils.getId(entity), UpgradeType.NEXT_ROUTE_TIME));
      });
  }

}
