package com.soze.idlekluch.kingdom.repository;

import com.soze.idlekluch.exception.EntityAlreadyExistsException;
import com.soze.idlekluch.exception.EntityDoesNotExistException;
import com.soze.idlekluch.kingdom.entity.Kingdom;

public interface KingdomRepository {

  /**
   * Adds a kingdom.
   * @param kingdom
   * @throws EntityAlreadyExistsException if a kingdom with given name already exists.
   */
  public void addKingdom(final Kingdom kingdom);

  /**
   * Checks if a kingdom name is available.
   * @param name
   * @return
   */
  public boolean isNameAvailable(final String name);

  /**
   * Deletes the kingdom.
   * @param kingdom
   * @throws EntityDoesNotExistException if kingdom does not exist
   */
  public void removeKingdom(final Kingdom kingdom);

  /**
   * Updates a given kingdom.
   * @param kingdom
   * @throws EntityDoesNotExistException if kingdom does not exist
   */
  public void updateKingdom(final Kingdom kingdom);



}
