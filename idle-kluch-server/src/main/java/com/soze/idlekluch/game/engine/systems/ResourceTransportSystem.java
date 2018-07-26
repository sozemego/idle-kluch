package com.soze.idlekluch.game.engine.systems;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.EntityUtils;
import com.soze.idlekluch.game.engine.components.ResourceStorageComponent;
import com.soze.idlekluch.game.engine.components.resourcetransport.ResourceRoute;
import com.soze.idlekluch.game.engine.nodes.Nodes;
import com.soze.klecs.engine.Engine;
import com.soze.klecs.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;


public class ResourceTransportSystem extends BaseEntitySystem {

  private static final Logger LOG = LoggerFactory.getLogger(ResourceTransportSystem.class);

  private static final float METERS_PER_SECOND = 10f;

  public ResourceTransportSystem(final Engine engine,
                                 final Map<EntityUUID, Entity> changedEntities) {
    super(engine, changedEntities);
  }

  @Override
  public void update(final float delta) {
    getStorages().forEach(entity -> update(entity, delta));
  }

  private void update(final Entity entity, final float delta) {
    final ResourceStorageComponent storage = entity.getComponent(ResourceStorageComponent.class);
    // transport to sellers if possible
    if (!storage.getRoutes().isEmpty()) {
      final ResourceRoute route = storage.getRoutes().get(0);
      final Entity target = getEngine().getEntityById(route.getTo()).get();
      final float distance = EntityUtils.distance(entity, target);
      final float progress = route.getProgress();
      final float distanceChange = METERS_PER_SECOND * delta;
      final float progressChange = distanceChange / distance;
      final float nextProgress = progress + progressChange;
      route.setProgress(nextProgress);

      if(nextProgress >= 0.99f) {
        LOG.trace("Finished route [{}]", route);
        final ResourceStorageComponent targetStorage = target.getComponent(ResourceStorageComponent.class);
        targetStorage.addResource(route.getResource());
        storage.removeRoute(route);
      }
    }
  }

  private List<Entity> getStorages() {
    return getEngine().getEntitiesByNode(Nodes.STORAGE);
  }

}
