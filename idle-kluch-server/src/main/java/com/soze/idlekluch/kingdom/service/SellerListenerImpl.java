package com.soze.idlekluch.kingdom.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.soze.idlekluch.core.aop.annotations.Profiled;
import com.soze.idlekluch.core.routes.Routes;
import com.soze.idlekluch.game.message.ModifyKingdomBucksMessage;
import com.soze.idlekluch.game.service.WebSocketMessagingService;
import com.soze.idlekluch.kingdom.events.ResourceSoldEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class SellerListenerImpl implements SellerListener {

  private final KingdomService kingdomService;
  private final WebSocketMessagingService webSocketMessagingService;

  private final Cache<String, Object> locks = CacheBuilder
                                                .newBuilder()
                                                .maximumSize(500)
                                                .expireAfterAccess(5, TimeUnit.MINUTES)
                                                .build();

  @Autowired
  public SellerListenerImpl(final KingdomService kingdomService,
                            final WebSocketMessagingService webSocketMessagingService) {

    this.kingdomService = Objects.requireNonNull(kingdomService);
    this.webSocketMessagingService = Objects.requireNonNull(webSocketMessagingService);
  }

  @Override
  @Profiled
  public void handleResourceSoldEvent(final ResourceSoldEvent event) {
    synchronized (getLock(event.getKingdomId().toString())) {
      final int price = event.getResource().getPrice();
      kingdomService
        .getKingdom(event.getKingdomId())
        .ifPresent(kingdom -> {
          kingdom.setIdleBucks(kingdom.getIdleBucks() + price);
          kingdomService.updateKingdom(kingdom);
          webSocketMessagingService.sendToUser(kingdom.getOwner().getUsername(), Routes.GAME_OUTBOUND, new ModifyKingdomBucksMessage(price));
        });
    }
  }

  private Object getLock(final String str) {
    try {
      return locks.get(str, () -> new Object());
    } catch (ExecutionException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

}
