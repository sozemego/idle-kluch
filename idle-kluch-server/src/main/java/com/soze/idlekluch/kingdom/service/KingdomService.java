package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.exception.EntityAlreadyExistsException;
import com.soze.idlekluch.kingdom.dto.RegisterKingdomForm;
import com.soze.idlekluch.kingdom.exception.UserAlreadyHasKingdomException;
import com.soze.idlekluch.kingdom.exception.UserDoesNotHaveKingdomException;

public interface KingdomService {

  /**
   * Adds a kingdom.
   * @param owner
   * @param form
   * @throws EntityAlreadyExistsException if a kingdom with given name already exists.
   * @throws UserAlreadyHasKingdomException if this user already has a kingdom
   */
  public void addKingdom(final String owner, final RegisterKingdomForm form);

  /**
   * Checks if a kingdom name is available.
   * @param name
   * @return
   */
  public boolean isNameAvailable(final String name);

  /**
   * Deletes the kingdom.
   * @param owner
   * @throws UserDoesNotHaveKingdomException if kingdom does not exist
   */
  public void removeKingdom(final String owner);

}
