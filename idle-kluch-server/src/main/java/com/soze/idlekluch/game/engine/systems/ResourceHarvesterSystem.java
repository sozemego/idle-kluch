package com.soze.idlekluch.game.engine.systems;

import com.soze.idlekluch.core.utils.MathUtils;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.components.ResourceHarvesterComponent;
import com.soze.idlekluch.game.engine.components.ResourceHarvesterComponent.HarvestingProgress;
import com.soze.idlekluch.game.engine.components.ResourceHarvesterComponent.HarvestingState;
import com.soze.idlekluch.game.engine.components.ResourceStorageComponent;
import com.soze.idlekluch.game.engine.nodes.Nodes;
import com.soze.klecs.engine.Engine;
import com.soze.klecs.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ResourceHarvesterSystem extends BaseEntitySystem {

  private static final Logger LOG = LoggerFactory.getLogger(ResourceHarvesterSystem.class);

  public ResourceHarvesterSystem(final Engine engine) {
    super(engine);
  }

  @Override
  public void update(final float delta) {
    getHarvesters().forEach((entity -> update(entity, delta)));
  }

  /**
   * A simple state machine describes the possible states of harvesting.
   * If entity is waiting and it has no free space in warehouse, it stays in waiting state.
   * If entity is waiting and has free space, it starts harvesting.
   * If entity is harvesting, but harvesting percentage is below 1, keep harvesting.
   * If entity is harvesting and harvesting percentage becomes 1, store the resource in warehouse and reset percentage to 0, start waiting.
   */
  private void update(final Entity entity, final float delta) {
    final HarvestingProgress currentHarvestingProgress = getCurrentHarvestingProgress(entity);
    final ResourceStorageComponent storage = entity.getComponent(ResourceStorageComponent.class);
    final int remainingCapacity = storage.getCapacity() - storage.getResources().size();
    if(remainingCapacity > 0 && currentHarvestingProgress.getHarvestingState() == HarvestingState.WAITING) {
      currentHarvestingProgress.setHarvestingState(HarvestingState.HARVESTING);
      currentHarvestingProgress.setHarvestingProgressPercent(0f);
    }

    if(currentHarvestingProgress.getHarvestingState() == HarvestingState.HARVESTING) {
      final ResourceHarvesterComponent resourceHarvesterComponent = entity.getComponent(ResourceHarvesterComponent.class);
      final int unitsPerMinute = resourceHarvesterComponent.getUnitsPerMinute();
      final float secondsPerUnit = 60 / (float) unitsPerMinute;
      final float harvestingPercentageChange = delta / secondsPerUnit;
      final float nextHarvestingPercentage = Math.min(1f, currentHarvestingProgress.getHarvestingProgressPercent() + harvestingPercentageChange);
      currentHarvestingProgress.setHarvestingProgressPercent(nextHarvestingPercentage);
      LOG.debug("Next harvesting percentage for [{}]: [{}]", entity.getId(), nextHarvestingPercentage);
    }

    if(currentHarvestingProgress.isFinished() && currentHarvestingProgress.getHarvestingState() == HarvestingState.HARVESTING) {
      currentHarvestingProgress.setHarvestingState(HarvestingState.WAITING);
      final ResourceHarvesterComponent harvester = entity.getComponent(ResourceHarvesterComponent.class);
      storage.addResource(harvester.getResource());
      LOG.debug("FINISHED HARVESTING FOR ENTITY [{}]", entity.getId());
    }
  }

  private HarvestingProgress getCurrentHarvestingProgress(final Entity entity) {
    return entity.getComponent(ResourceHarvesterComponent.class).getHarvestingProgress();
  }

  private List<Entity> getHarvesters() {
    return getEngine().getEntitiesByNode(Nodes.HARVESTER);
  }

}
