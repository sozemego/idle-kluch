package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.core.exception.EntityDoesNotExistException;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.game.exception.*;
import com.soze.idlekluch.game.message.AttachResourceSourceForm;
import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.kingdom.events.KingdomAddedEvent;
import com.soze.klecs.engine.RemovedEntityEvent;
import com.soze.klecs.entity.Entity;

import java.util.List;

public interface BuildingService {

  /**
   * Attempts to place a building for a given player.
   */
  Entity buildBuilding(String owner, BuildBuildingForm form);

  List<Entity> getOwnBuildings(String owner);

  List<Entity> getAllConstructableBuildings();

  /**
   * Returns all buildings constructed in the game, from the DB.
   */
  List<PersistentEntity> getAllConstructedBuildings();

  /**
   * For given entity, attaches a resource source at a given slot number.
   * @throws InvalidOwnerException if given owner is not the actual owner of harvester
   * @throws EntityDoesNotHaveComponentException if harvester does not have ResourceHarvesterComponent
   *                                              or source does not have ResourceSourceComponent
   * @throws EntityDoesNotExistException if harvester or source do not exist
   * @throws InvalidResourceException if given harvester cannot harvesting a resource supplied by this source
   * @throws NoResourceInRadiusException if the resource to attach to is further away than harvester radius
   * @throws InvalidResourceSlotException if the slot to attach does not exist
   */
  void attachResourceSource(String owner, AttachResourceSourceForm form);

  /**
   * Attempts to delete a building with given buildingId.
   */
  void destroyBuilding(EntityUUID buildingId);

  void handleRemovedEntity(RemovedEntityEvent removedEntityEvent);

  /**
   * When a kingdom is created, we want to create the first building for this kingdom.
   */
  void handleKingdomAddedEvent(KingdomAddedEvent kingdomAddedEvent);

}
