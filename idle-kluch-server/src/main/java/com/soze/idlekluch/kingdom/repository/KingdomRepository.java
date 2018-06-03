package com.soze.idlekluch.kingdom.repository;

import com.soze.idlekluch.core.exception.EntityAlreadyExistsException;
import com.soze.idlekluch.core.exception.EntityDoesNotExistException;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import java.util.List;
import java.util.Optional;

public interface KingdomRepository {

  /**
   * Adds a kingdom.
   * @param kingdom
   * @throws EntityAlreadyExistsException if a kingdom with given name already exists.
   */
  void addKingdom(Kingdom kingdom);

  Optional<Kingdom> getKingdom(String name);

  Optional<Kingdom> getKingdom(EntityUUID kingdomId);

  List<Kingdom> getAllKingdoms();

  Optional<Kingdom> getUsersKingdom(String username);

  /**
   * Deletes kingdom with given name.
   * @throws EntityDoesNotExistException if kingdom does not exist
   */
  void removeKingdom(String name);

  /**
   * Updates a given kingdom.
   * @param kingdom
   * @throws EntityDoesNotExistException if kingdom does not exist
   */
  void updateKingdom(Kingdom kingdom);

}
