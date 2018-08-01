package com.soze.idlekluch.game.engine.systems;

import com.soze.idlekluch.core.event.EventPublisher;
import com.soze.idlekluch.core.routes.Routes;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.components.OwnershipComponent;
import com.soze.idlekluch.game.engine.components.ResourceSellerComponent;
import com.soze.idlekluch.game.engine.components.ResourceStorageComponent;
import com.soze.idlekluch.game.engine.nodes.Nodes;
import com.soze.idlekluch.game.message.StartSellingMessage;
import com.soze.idlekluch.game.service.WebSocketMessagingService;
import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.idlekluch.kingdom.events.ResourceSoldEvent;
import com.soze.klecs.engine.Engine;
import com.soze.klecs.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ResourceSellerSystem extends BaseEntitySystem {

  private static final Logger LOG = LoggerFactory.getLogger(ResourceSellerSystem.class);

  private final WebSocketMessagingService webSocketMessagingService;
  private final EventPublisher eventPublisher;

  public ResourceSellerSystem(final Engine engine,
                              final Map<EntityUUID, Entity> changedEntities,
                              final WebSocketMessagingService webSocketMessagingService,
                              final EventPublisher eventPublisher) {
    super(engine, changedEntities);
    this.webSocketMessagingService = Objects.requireNonNull(webSocketMessagingService);
    this.eventPublisher = Objects.requireNonNull(eventPublisher);
  }

  @Override
  public void update(final float delta) {
    getSellers().forEach(seller -> update(seller, delta));
  }

  private void update(final Entity entity, final float delta) {
    final ResourceStorageComponent storage = entity.getComponent(ResourceStorageComponent.class);
    if (storage.getResources().isEmpty()) {
      //nothing to sell
      return;
    }

    final ResourceSellerComponent sellerComponent = entity.getComponent(ResourceSellerComponent.class);
    Resource resourceBeingSold = sellerComponent.getResourceBeingSold();
    if (resourceBeingSold != null) {
      final float secondsPerUnit = sellerComponent.getSecondsPerUnit();
      final float sellingPercentageChange = delta / secondsPerUnit;
      final float nextSellingPercentage = Math.min(1f, sellerComponent.getSellingProgress() + sellingPercentageChange);
      sellerComponent.setSellingProgress(nextSellingPercentage);
    }

    if (resourceBeingSold == null) {
      final List<Resource> resources = new ArrayList<>(storage.getResources());
      resources.sort(Comparator.comparingInt(Resource::getPrice));
      resourceBeingSold = resources.get(resources.size() - 1);
      sellerComponent.startSelling(resourceBeingSold);
      webSocketMessagingService.send(Routes.GAME_OUTBOUND, new StartSellingMessage((EntityUUID) entity.getId(), resourceBeingSold));
    }

    if (sellerComponent.isFinished()) {
      sellerComponent.stopSelling();
      storage.removeResource(resourceBeingSold);
      addChangedEntity(entity);
      final OwnershipComponent ownershipComponent = entity.getComponent(OwnershipComponent.class);
      eventPublisher.publishEvent(new ResourceSoldEvent(ownershipComponent.getOwnerId(), resourceBeingSold));
      LOG.trace("FINISHED SELLING [{}]", resourceBeingSold);
    }
  }

  private List<Entity> getSellers() {
    return getEngine().getEntitiesByNode(Nodes.SELLER);
  }
}
