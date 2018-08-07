package com.soze.idlekluch.game.upgrade.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.soze.idlekluch.game.engine.components.ResourceSellerComponent;
import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceHarvesterComponent;
import com.soze.idlekluch.game.upgrade.dto.Upgrade;
import com.soze.idlekluch.game.upgrade.repository.UpgradeRepository;
import com.soze.idlekluch.game.upgrade.service.UpgradeService.UpgradeType;
import com.soze.klecs.entity.Entity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Contains data about possible upgrades (cost, what will be upgraded etc).
 * The difference between this service and {@link UpgradeRepository} is that
 * the repository holds data about applied upgrades (basically, levels).
 */
@Service
public class UpgradeDataService {

  private final Multimap<UpgradeType, Upgrade> upgrades = ArrayListMultimap.create();

  @PostConstruct
  public void setup() {
    populateHarvesterSpeed();
    populateSellingSpeed();
  }

  public Optional<Upgrade> getUpgrade(final UpgradeType type, final int level) {
    final List<Upgrade> upgradeCosts = (List) upgrades.get(type);
    if (upgradeCosts.size() > level - 1) {
      return Optional.of(upgradeCosts.get(level - 1));
    }
    return Optional.empty();
  }

  public Map<UpgradeType, Collection<Upgrade>> getUpgrades() {
    return upgrades.asMap();
  }

  private void populateHarvesterSpeed() {
    final BiConsumer<Entity, Object> harvestSpeedConsumer = getHarvestSpeedConsumer();
    upgrades.put(UpgradeType.HARVESTER_SPEED, new Upgrade(UpgradeType.HARVESTER_SPEED, 1, 250, 1.2f, harvestSpeedConsumer));
    upgrades.put(UpgradeType.HARVESTER_SPEED, new Upgrade(UpgradeType.HARVESTER_SPEED, 2, 500, 1.2f, harvestSpeedConsumer));
    upgrades.put(UpgradeType.HARVESTER_SPEED, new Upgrade(UpgradeType.HARVESTER_SPEED, 3, 1000, 1.2f, harvestSpeedConsumer));
    upgrades.put(UpgradeType.HARVESTER_SPEED, new Upgrade(UpgradeType.HARVESTER_SPEED, 4, 2000, 1.4f, harvestSpeedConsumer));
    upgrades.put(UpgradeType.HARVESTER_SPEED, new Upgrade(UpgradeType.HARVESTER_SPEED, 5, 4000, 1.6f, harvestSpeedConsumer));
    upgrades.put(UpgradeType.HARVESTER_SPEED, new Upgrade(UpgradeType.HARVESTER_SPEED, 6, 8000, 1.6f, harvestSpeedConsumer));
    upgrades.put(UpgradeType.HARVESTER_SPEED, new Upgrade(UpgradeType.HARVESTER_SPEED, 7, 16000, 1.8f, harvestSpeedConsumer));
    upgrades.put(UpgradeType.HARVESTER_SPEED, new Upgrade(UpgradeType.HARVESTER_SPEED, 8, 32000, 1.8f, harvestSpeedConsumer));
    upgrades.put(UpgradeType.HARVESTER_SPEED, new Upgrade(UpgradeType.HARVESTER_SPEED, 9, 64000, 2f, harvestSpeedConsumer));
  }

  private BiConsumer<Entity, Object> getHarvestSpeedConsumer() {
    return (entity, data) -> {
      final ResourceHarvesterComponent harvesterComponent = entity.getComponent(ResourceHarvesterComponent.class);
      final float nextUnitsPerMinute = (float) Math.floor((harvesterComponent.getUnitsPerMinute() * (float) data) * 100) / 100;
      harvesterComponent.setUnitsPerMinute(nextUnitsPerMinute);
      harvesterComponent.setSpeedLevel(harvesterComponent.getSpeedLevel() + 1);
    };
  }

  private void populateSellingSpeed() {
    final int baseCost = 1000;
    final BiConsumer<Entity, Object> sellingSpeedConsumer = getSellingSpeedConsumer();
    upgrades.put(UpgradeType.SELLING_SPEED, new Upgrade(UpgradeType.SELLING_SPEED, 1, baseCost * 1, 1.5f, sellingSpeedConsumer));
    upgrades.put(UpgradeType.SELLING_SPEED, new Upgrade(UpgradeType.SELLING_SPEED, 2, baseCost * 2, 1.5f, sellingSpeedConsumer));
    upgrades.put(UpgradeType.SELLING_SPEED, new Upgrade(UpgradeType.SELLING_SPEED, 3, baseCost * 4, 1.5f, sellingSpeedConsumer));
    upgrades.put(UpgradeType.SELLING_SPEED, new Upgrade(UpgradeType.SELLING_SPEED, 4, baseCost * 8, 1.5f, sellingSpeedConsumer));
    upgrades.put(UpgradeType.SELLING_SPEED, new Upgrade(UpgradeType.SELLING_SPEED, 5, baseCost * 16, 1.75f, sellingSpeedConsumer));
    upgrades.put(UpgradeType.SELLING_SPEED, new Upgrade(UpgradeType.SELLING_SPEED, 6, baseCost * 32, 1.75f, sellingSpeedConsumer));
    upgrades.put(UpgradeType.SELLING_SPEED, new Upgrade(UpgradeType.SELLING_SPEED, 7, baseCost * 64, 1.75f, sellingSpeedConsumer));
    upgrades.put(UpgradeType.SELLING_SPEED, new Upgrade(UpgradeType.SELLING_SPEED, 8, baseCost * 256, 1.9f, sellingSpeedConsumer));
    upgrades.put(UpgradeType.SELLING_SPEED, new Upgrade(UpgradeType.SELLING_SPEED, 9, baseCost * 1000, 2f, sellingSpeedConsumer));
  }

  private BiConsumer<Entity, Object> getSellingSpeedConsumer() {
    return (entity, data) -> {
      final ResourceSellerComponent seller = entity.getComponent(ResourceSellerComponent.class);
      final float nextSecondsPerUnit = (float) Math.floor(seller.getSecondsPerUnit() * (1 / (float) data) * 100) / 100;
      seller.setSecondsPerUnit(nextSecondsPerUnit);
      seller.setSpeedLevel(seller.getSpeedLevel() + 1);
    };
  }

}
