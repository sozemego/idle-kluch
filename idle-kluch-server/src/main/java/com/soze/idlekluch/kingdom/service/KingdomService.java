package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.exception.EntityAlreadyExistsException;
import com.soze.idlekluch.kingdom.dto.RegisterKingdomForm;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.exception.UserAlreadyHasKingdomException;
import com.soze.idlekluch.kingdom.exception.UserDoesNotHaveKingdomException;

import java.util.Optional;

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
   * Deletes the kingdom.
   * @param owner
   * @throws UserDoesNotHaveKingdomException if kingdom does not exist
   */
  public void removeKingdom(final String owner);

  public Optional<Kingdom> getKingdom(final String name);

  public Optional<Kingdom> getUsersKingdom(final String username);

}
