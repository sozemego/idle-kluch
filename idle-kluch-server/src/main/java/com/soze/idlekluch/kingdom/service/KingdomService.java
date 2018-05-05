package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.exception.EntityAlreadyExistsException;
import com.soze.idlekluch.kingdom.dto.RegisterKingdomForm;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.exception.UserAlreadyHasKingdomException;
import com.soze.idlekluch.kingdom.exception.UserDoesNotHaveKingdomException;
import com.soze.idlekluch.world.entity.TileId;

import java.util.Optional;

public interface KingdomService {

  /**
   * Adds a kingdom.
   * Returns the starting point of the kingdom as a {@link TileId}.
   * @throws EntityAlreadyExistsException if a kingdom with given name already exists.
   * @throws UserAlreadyHasKingdomException if this user already has a kingdom
   */
  public void addKingdom(final String owner, final RegisterKingdomForm form);

  /**
   * Deletes the kingdom.
   * @throws UserDoesNotHaveKingdomException if kingdom does not exist
   */
  public void removeKingdom(final String owner);

  /**
   * Retrieves {@link Kingdom} by kingdom name.
   */
  public Optional<Kingdom> getKingdom(final String name);

  /**
   * Retrieves {@link Kingdom} by username.
   */
  public Optional<Kingdom> getUsersKingdom(final String username);

}
