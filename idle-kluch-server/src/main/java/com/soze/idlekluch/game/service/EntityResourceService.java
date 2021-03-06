package com.soze.idlekluch.game.service;

import com.soze.idlekluch.core.exception.EntityDoesNotExistException;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.components.ResourceSourceComponent;
import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.idlekluch.world.events.WorldChunkCreatedEvent;
import com.soze.klecs.entity.Entity;

import java.awt.*;
import java.util.List;

public interface EntityResourceService {


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
   * Returns all entities which are sources of any {@link Resource}.
   */
  List<Entity> getAllResourceSources();

  /**
   * Returns all entities which are sources of this particular {@link Resource}.
   */
  List<Entity> getAllResourceSources(Resource resource);

  void handleWorldChunkCreatedEvent(WorldChunkCreatedEvent worldChunkCreatedEvent);

  List<Entity> getResourceSourcesInRadius(Resource resource, Point center, int radius);

  List<Entity> getResourceSourcesInRadius(EntityUUID resourceId, Point center, int radius);

  /**
   * Attempts to find an entity template with given id at a given position.
   * @throws EntityDoesNotExistException if there is no template with entityId
   */
  Entity placeResourceSource(EntityUUID entityId, Point position);

}
