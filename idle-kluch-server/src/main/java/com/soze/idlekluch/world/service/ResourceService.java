package com.soze.idlekluch.world.service;

import com.soze.idlekluch.world.events.WorldChunkCreatedEvent;

public interface ResourceService {

  void handleWorldChunkCreatedEvent(WorldChunkCreatedEvent worldChunkCreatedEvent);

}
