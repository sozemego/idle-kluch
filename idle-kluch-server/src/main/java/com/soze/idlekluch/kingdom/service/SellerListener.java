package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.kingdom.events.ResourceSoldEvent;
import org.springframework.context.event.EventListener;

/**
 * Objects implementing this interface listen to {@link ResourceSoldEvent}'s,
 * update kingdoms and send kingdom money updates.
 */
public interface SellerListener {

  @EventListener
  void handleResourceSoldEvent(ResourceSoldEvent event);

}
