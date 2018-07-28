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

import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ResourceTransportSystem extends BaseEntitySystem {

  private static final Logger LOG = LoggerFactory.getLogger(ResourceTransportSystem.class);

  private static final float METERS_PER_SECOND = 30f;

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
    final Iterator<ResourceRoute> it = storage.getRoutes().iterator();
    while (it.hasNext()) {
      final ResourceRoute route = it.next();
      final Entity target = getEngine().getEntityById(route.getTo()).get();
      final float distance = EntityUtils.distance(entity, target);
      final float progress = route.getProgress();
      final float distanceChange = METERS_PER_SECOND * delta;
      final float progressChange = distanceChange / distance;
      final float nextProgress = progress + progressChange;
      route.setProgress(Math.min(1f, nextProgress));

      final ResourceStorageComponent targetStorage = target.getComponent(ResourceStorageComponent.class);
      if(nextProgress >= 0.99f && targetStorage.hasRemainingCapacity()) {
        LOG.trace("Finished route [{}]", route);
        targetStorage.addResource(route.getResource());
        it.remove();
      }
    }
  }

  private List<Entity> getStorages() {
    return getEngine().getEntitiesByNode(Nodes.STORAGE);
  }

}
