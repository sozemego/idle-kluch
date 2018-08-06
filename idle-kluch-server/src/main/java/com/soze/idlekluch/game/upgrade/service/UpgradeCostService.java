package com.soze.idlekluch.game.upgrade.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.soze.idlekluch.game.upgrade.service.UpgradeService.UpgradeType;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Stores upgrade costs for each possible upgrade.
 */
@Service
public class UpgradeCostService {

  private Multimap<UpgradeType, Integer> costs = ArrayListMultimap.create();

  @PostConstruct
  public void setup() {
    populateHarvesterSpeedCosts();
  }

  public Optional<Integer> getUpgradeCost(final UpgradeType type, final int level) {
    final List<Integer> upgradeCosts = (ArrayList) costs.get(type);
    if (upgradeCosts.size() > level - 1) {
      return Optional.of(upgradeCosts.get(level - 1));
    }
    return Optional.empty();
  }

  private void populateHarvesterSpeedCosts() {
    costs.put(UpgradeType.HARVESTER_SPEED, 250);
    costs.put(UpgradeType.HARVESTER_SPEED, 500);
    costs.put(UpgradeType.HARVESTER_SPEED, 1000);
    costs.put(UpgradeType.HARVESTER_SPEED, 2000);
    costs.put(UpgradeType.HARVESTER_SPEED, 4000);
    costs.put(UpgradeType.HARVESTER_SPEED, 8000);
    costs.put(UpgradeType.HARVESTER_SPEED, 16000);
    costs.put(UpgradeType.HARVESTER_SPEED, 32000);
    costs.put(UpgradeType.HARVESTER_SPEED, 64000);
  }

}
