package com.soze.idlekluch.game.engine.systems;

import com.soze.idlekluch.core.routes.Routes;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.components.OwnershipComponent;
import com.soze.idlekluch.game.engine.components.ResourceSellerComponent;
import com.soze.idlekluch.game.engine.components.ResourceStorageComponent;
import com.soze.idlekluch.game.engine.components.resourcetransport.ResourceRoute;
import com.soze.idlekluch.game.engine.nodes.Nodes;
import com.soze.idlekluch.game.message.TransferResourceMessage;
import com.soze.idlekluch.game.service.WebSocketMessagingService;
import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.klecs.engine.Engine;
import com.soze.klecs.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.acl.Owner;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.soze.idlekluch.game.engine.EntityUtils.*;

/**
 * This system is responsible for sending the resources from {@link ResourceStorageComponent},
 * to other entities. This system only decides where to send the resources.
 */
public class ResourceStorageSystem extends BaseEntitySystem {

  private static final Logger LOG = LoggerFactory.getLogger(ResourceStorageSystem.class);
  private final WebSocketMessagingService webSocketMessagingService;

  public ResourceStorageSystem(final Engine engine,
                               final Map<EntityUUID, Entity> changedEntities,
                               final WebSocketMessagingService webSocketMessagingService) {
    super(engine, changedEntities);
    this.webSocketMessagingService = Objects.requireNonNull(webSocketMessagingService);
  }

  @Override
  public void update(final float delta) {
    getStorages().forEach(entity -> update(entity, delta));
  }

  private void update(final Entity entity, final float delta) {
    final ResourceStorageComponent storage = entity.getComponent(ResourceStorageComponent.class);
    final boolean isSeller = entity.getComponent(ResourceSellerComponent.class) != null;
    if (isSeller) {
      return;
    }

    if (storage.getRoutes().size() < storage.getMaxRoutes()) {
      updateNextRouteProgress(storage, delta);
    } else {
      return;
    }

    if (storage.getNextRouteProgress() < storage.getSecondsPerRoute()) {
      return;
    }
    // transport to sellers if possible
    if (!storage.getResources().isEmpty()) {
      getSellers()
        .stream()
        .filter(seller -> !seller.getId().equals(entity.getId()))
        .filter(seller -> {
          final OwnershipComponent sellerOwnership = seller.getComponent(OwnershipComponent.class);
          final OwnershipComponent sellingOwnership = entity.getComponent(OwnershipComponent.class);
          return sellerOwnership.getOwnerId().equals(sellingOwnership.getOwnerId());
        })
        .filter(seller -> {
          final ResourceStorageComponent sellerStorage = seller.getComponent(ResourceStorageComponent.class);
          return sellerStorage.hasRemainingCapacity();
        })
        .sorted(Comparator.comparingInt(e -> {
          final ResourceStorageComponent sellerStorage = e.getComponent(ResourceStorageComponent.class);
          return sellerStorage.getRemainingCapacity();
        }))
        .sorted(Comparator.comparingInt(e -> (int) distance(e, entity)))
        .findFirst()
        .ifPresent(seller -> {
          storage.setNextRouteProgress(0f);

          final Resource resourceToTransfer = storage.getResources().get(0);
          final ResourceRoute route = new ResourceRoute(

            resourceToTransfer, getId(entity), getId(seller)
          );
          storage.addRoute(route);
          storage.removeResource(resourceToTransfer);

          webSocketMessagingService.send(Routes.GAME_OUTBOUND, new TransferResourceMessage(route));

          LOG.trace("Transferring [{}] from [{}] to [{}]. [{}]", resourceToTransfer, getName(entity), getName(seller), route);
        });
    }
  }

  private List<Entity> getStorages() {
    return getEngine().getEntitiesByNode(Nodes.STORAGE);
  }

  private List<Entity> getSellers() {
    return getEngine().getEntitiesByNode(Nodes.SELLER);
  }

  private void updateNextRouteProgress(final ResourceStorageComponent storageComponent, final float delta) {
    final float nextRouteProgress = storageComponent.getNextRouteProgress();
    storageComponent.setNextRouteProgress(Math.min(storageComponent.getSecondsPerRoute(), nextRouteProgress + delta));
  }

}
