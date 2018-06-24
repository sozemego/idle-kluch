package com.soze.idlekluch.game.engine.systems;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.components.ResourceHarvesterComponent;
import com.soze.idlekluch.game.engine.nodes.Nodes;
import com.soze.klecs.engine.Engine;
import com.soze.klecs.entity.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceHarvesterSystem extends BaseEntitySystem {

  private final Map<EntityUUID, Float> productionMap = new HashMap<>();

  public ResourceHarvesterSystem(final Engine engine) {
    super(engine);
  }

  @Override
  public void update(final float delta) {
    getHarvesters().forEach((entity -> update(entity, delta)));
  }

  private void update(final Entity entity, final float delta) {
    final float currentProductionPercent = getCurrentProductionProgress(entity);
    if(currentProductionPercent == 1f) {
      productionMap.put((EntityUUID) entity.getId(), 0f);
      System.out.println("FINISHED PRODUCTION FOR ENTITY " + entity.getId());
      return;
    }
    final ResourceHarvesterComponent resourceHarvesterComponent = entity.getComponent(ResourceHarvesterComponent.class);
    final int unitsPerMinute = resourceHarvesterComponent.getUnitsPerMinute();
    final float secondsPerUnit = 60 / (float) unitsPerMinute;
    final float productionPercentageChange = delta / secondsPerUnit;
    final float nextProductionPercentage = Math.min(1f, currentProductionPercent + productionPercentageChange);
    System.out.println(nextProductionPercentage);
    productionMap.put((EntityUUID) entity.getId(), nextProductionPercentage);
  }

  /**
   * Returns progress of production, as a percentage from 0 to 1.
   * @param entity
   * @return
   */
  private float getCurrentProductionProgress(final Entity entity) {
    return productionMap.computeIfAbsent((EntityUUID) entity.getId(), k -> 0f);
  }

  private List<Entity> getHarvesters() {
    return getEngine().getEntitiesByNode(Nodes.HARVESTER);
  }

}
