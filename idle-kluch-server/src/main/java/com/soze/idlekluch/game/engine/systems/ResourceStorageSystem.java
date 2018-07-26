package com.soze.idlekluch.game.engine.systems;

import com.soze.idlekluch.core.routes.Routes;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.EntityUtils;
import com.soze.idlekluch.game.engine.components.ResourceStorageComponent;
import com.soze.idlekluch.game.engine.nodes.Nodes;
import com.soze.idlekluch.game.message.TransferResourceMessage;
import com.soze.idlekluch.game.service.WebSocketMessagingService;
import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.klecs.engine.Engine;
import com.soze.klecs.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This system is responsible for sending the resources from {@link ResourceStorageComponent},
 * to other entities.
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
    // transport to sellers if possible
    if (!storage.getResources().isEmpty()) {
      getSellers()
        .stream()
        .filter(seller -> {
          final ResourceStorageComponent sellerStorage = seller.getComponent(ResourceStorageComponent.class);
          return sellerStorage.getRemainingCapacity() > 0;
        })
        .sorted(Comparator.comparingInt(e -> (int) EntityUtils.distance(e, entity)))
        .findFirst()
        .ifPresent(seller -> {
          final ResourceStorageComponent sellerStorage = seller.getComponent(ResourceStorageComponent.class);
          final Resource resourceToTransfer = storage.getResources().get(0);
          storage.removeResource(resourceToTransfer);
          sellerStorage.addResource(resourceToTransfer);
          webSocketMessagingService.send(Routes.GAME_OUTBOUND,
            new TransferResourceMessage(
              (EntityUUID) entity.getId(), (EntityUUID) seller.getId(), resourceToTransfer
            )
          );

          LOG.trace("Transferring [] from [] to []", resourceToTransfer, EntityUtils.getName(entity), EntityUtils.getName(seller));
        });
    }
  }

  private List<Entity> getStorages() {
    return getEngine().getEntitiesByNode(Nodes.STORAGE);
  }

  private List<Entity> getSellers() {
    return getEngine().getEntitiesByNode(Nodes.SELLER);
  }

}
