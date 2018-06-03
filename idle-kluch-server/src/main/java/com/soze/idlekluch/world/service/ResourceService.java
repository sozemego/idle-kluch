package com.soze.idlekluch.world.service;

import com.soze.idlekluch.game.engine.components.ResourceSourceComponent;
import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.idlekluch.world.events.WorldChunkCreatedEvent;
import com.soze.klecs.entity.Entity;

import java.util.List;

public interface ResourceService {

  List<Entity> getAllResourceEntityTemplates();

  /**
   * Returns all entity templates that have {@link ResourceSourceComponent}
   * which provides a given resource.
   *
   * NOTE. Don't add templates to the engine, copy them and add copies.
   */
  List<Entity> getResourceEntityTemplates(Resource resource);

  /**
   * Returns all entity templates that have {@link ResourceSourceComponent}
   * which provides a resource with given name.
   *
   * NOTE. Don't add templates to the engine, copy them and add copies.
   */
  List<Entity> getResourceEntityTemplates(String resourceName);

  /**
   * Returns all entities which are sources of {@link Resource};
   * @return
   */
  List<Entity> getAllResourceSources();

  void handleWorldChunkCreatedEvent(WorldChunkCreatedEvent worldChunkCreatedEvent);

}
