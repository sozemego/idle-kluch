package com.soze.idlekluch.game.upgrade.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.soze.idlekluch.game.upgrade.dto.Upgrade;
import com.soze.idlekluch.game.upgrade.service.UpgradeService.UpgradeType;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UpgradeDataService {

  private final Multimap<UpgradeType, Upgrade> upgrades = ArrayListMultimap.create();

  @PostConstruct
  public void setup() {
    populateHarvesterSpeed();
  }

  public Optional<Upgrade> getUpgrade(final UpgradeType type, final int level) {
    final List<Upgrade> upgradeCosts = (ArrayList) upgrades.get(type);
    if (upgradeCosts.size() > level - 1) {
      return Optional.of(upgradeCosts.get(level - 1));
    }
    return Optional.empty();
  }

  private void populateHarvesterSpeed() {
    upgrades.put(UpgradeType.HARVESTER_SPEED, new Upgrade(UpgradeType.HARVESTER_SPEED, 1, 250, 1.2f));
    upgrades.put(UpgradeType.HARVESTER_SPEED, new Upgrade(UpgradeType.HARVESTER_SPEED, 1, 500, 1.2f));
    upgrades.put(UpgradeType.HARVESTER_SPEED, new Upgrade(UpgradeType.HARVESTER_SPEED, 1, 1000, 1.3f));
    upgrades.put(UpgradeType.HARVESTER_SPEED, new Upgrade(UpgradeType.HARVESTER_SPEED, 1, 2000, 1.3f));
    upgrades.put(UpgradeType.HARVESTER_SPEED, new Upgrade(UpgradeType.HARVESTER_SPEED, 1, 4000, 1.4f));
    upgrades.put(UpgradeType.HARVESTER_SPEED, new Upgrade(UpgradeType.HARVESTER_SPEED, 1, 8000, 1.4f));
    upgrades.put(UpgradeType.HARVESTER_SPEED, new Upgrade(UpgradeType.HARVESTER_SPEED, 1, 16000, 1.6f));
    upgrades.put(UpgradeType.HARVESTER_SPEED, new Upgrade(UpgradeType.HARVESTER_SPEED, 1, 32000, 1.6f));
    upgrades.put(UpgradeType.HARVESTER_SPEED, new Upgrade(UpgradeType.HARVESTER_SPEED, 1, 64000, 2f));
  }

}
