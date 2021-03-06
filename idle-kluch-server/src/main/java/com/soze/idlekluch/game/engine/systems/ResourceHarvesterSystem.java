package com.soze.idlekluch.game.engine.systems;

import com.soze.idlekluch.core.routes.Routes;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.EntityUtils;
import com.soze.idlekluch.game.engine.components.ResourceSourceComponent;
import com.soze.idlekluch.game.engine.components.ResourceStorageComponent;
import com.soze.idlekluch.game.engine.components.resourceharvester.HarvestingProgress;
import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceHarvesterComponent;
import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceSourceSlot;
import com.soze.idlekluch.game.engine.nodes.Nodes;
import com.soze.idlekluch.game.message.StartHarvestingMessage;
import com.soze.idlekluch.game.message.TransferResourceMessage;
import com.soze.idlekluch.game.service.WebSocketMessagingService;
import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.idlekluch.world.service.ResourceService;
import com.soze.klecs.engine.Engine;
import com.soze.klecs.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ResourceHarvesterSystem extends BaseEntitySystem {

  private static final Logger LOG = LoggerFactory.getLogger(ResourceHarvesterSystem.class);

  private final WebSocketMessagingService webSocketMessagingService;
  private final ResourceService resourceService;

  private final List<EntityUUID> beganHarvesting = new ArrayList<>();

  public ResourceHarvesterSystem(final Engine engine,
                                 final Map<EntityUUID, Entity> changedEntities,
                                 final WebSocketMessagingService webSocketMessagingService,
                                 final ResourceService resourceService) {

    super(engine, changedEntities);
    this.webSocketMessagingService = Objects.requireNonNull(webSocketMessagingService);
    this.resourceService = Objects.requireNonNull(resourceService);
  }

  @Override
  public void update(final float delta) {
    beganHarvesting.clear();
    getHarvesters().forEach((entity -> update(entity, delta)));
    beganHarvesting.forEach(id -> webSocketMessagingService.send(Routes.GAME_OUTBOUND, new StartHarvestingMessage(id)));
  }

  /**
   * A simple state machine describes the possible states of harvesting.
   * If entity is waiting and it has no free space in warehouse, it stays in waiting state.
   * If entity is waiting and has free space, it starts harvesting.
   * If entity is harvesting, but harvesting percentage is below 1, keep harvesting.
   * If entity is harvesting and harvesting percentage becomes 1, store the resource in warehouse and reset percentage to 0, start waiting.
   *
   * At the end, if possible, resources are transferred to sellers.
   */
  private void update(final Entity entity, final float delta) {
    final HarvestingProgress currentHarvestingProgress = getCurrentHarvestingProgress(entity);
    final ResourceStorageComponent storage = entity.getComponent(ResourceStorageComponent.class);

    if(currentHarvestingProgress.getHarvestingState() == HarvestingProgress.HarvestingState.HARVESTING) {
      final float secondsPerUnit = getSecondsPerUnit(entity);
      final float harvestingPercentageChange = delta / secondsPerUnit;
      final float nextHarvestingPercentage = Math.min(1f, currentHarvestingProgress.getHarvestingProgressPercent() + harvestingPercentageChange);
      currentHarvestingProgress.setHarvestingProgressPercent(nextHarvestingPercentage);
    }

    if (currentHarvestingProgress.isFinished() && currentHarvestingProgress.getHarvestingState() == HarvestingProgress.HarvestingState.HARVESTING) {
      currentHarvestingProgress.stop();
      final ResourceHarvesterComponent harvester = entity.getComponent(ResourceHarvesterComponent.class);
      final Resource resource = resourceService.getResource(harvester.getResourceId()).get();
      storage.addResource(resource);
      addChangedEntity(entity);
      LOG.trace("FINISHED HARVESTING FOR ENTITY [{}][{}][{}]/[{}]", entity.getId(), EntityUtils.getName(entity), storage.getResources().size(), storage.getCapacity());
    }

    final int remainingCapacity = storage.getCapacity() - storage.getResources().size();
    if(remainingCapacity > 0 && currentHarvestingProgress.getHarvestingState() == HarvestingProgress.HarvestingState.WAITING) {
      currentHarvestingProgress.start();
      beganHarvesting.add((EntityUUID) entity.getId());
    }
  }

  private HarvestingProgress getCurrentHarvestingProgress(final Entity entity) {
    return entity.getComponent(ResourceHarvesterComponent.class).getHarvestingProgress();
  }

  private List<Entity> getHarvesters() {
    return getEngine().getEntitiesByNode(Nodes.HARVESTER);
  }

  /**
   * Determines the speed of harvesting based on how many resource sources are attached to this harvester,
   * but also how many harvesters are attached to the sources.
   */
  private float getSecondsPerUnit(final Entity entity) {
    final ResourceHarvesterComponent resourceHarvesterComponent = entity.getComponent(ResourceHarvesterComponent.class);
    final float unitsPerMinute = resourceHarvesterComponent.getUnitsPerMinute();
    final float bonus = getBonus(resourceHarvesterComponent.getSlots());
    float secondsPerUnit = 60 / (unitsPerMinute * bonus);
    return secondsPerUnit;
  }

  private float getBonus(final Collection<ResourceSourceSlot> sourceIds) {
    return sourceIds
             .stream()
             .filter(slot -> slot.getSourceId() != null)
             .map(slot -> getEngine().getEntityById(slot.getSourceId()).get())
             .map(source -> {
               final ResourceSourceComponent resourceSourceComponent = source.getComponent(ResourceSourceComponent.class);
               return resourceSourceComponent.getBonus();
             })
             .reduce(1f, (prev, next) -> prev * next);
  }

}
