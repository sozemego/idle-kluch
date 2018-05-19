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
   * Distance in tiles between kingdoms. When new kingdom is created, it will be at least
   * this many tiles away from any buildings belonging to other kingdoms.
   */
  int MINIMUM_DISTANCE_BETWEEN_KINGDOMS = 10;

  long STARTING_IDLE_BUCKS = 500;

  /**
   * Adds a kingdom.
   * Returns the starting point of the kingdom as a {@link TileId}.
   * @throws EntityAlreadyExistsException if a kingdom with given name already exists.
   * @throws UserAlreadyHasKingdomException if this user already has a kingdom
   */
  void addKingdom(final String owner, final RegisterKingdomForm form);

  /**
   * Deletes the kingdom.
   * @throws UserDoesNotHaveKingdomException if kingdom does not exist
   */
  void removeKingdom(final String owner);

  void updateKingdom(final Kingdom kingdom);

  /**
   * Retrieves {@link Kingdom} by kingdom name.
   */
  Optional<Kingdom> getKingdom(final String name);

  /**
   * Retrieves {@link Kingdom} by username.
   */
  Optional<Kingdom> getUsersKingdom(final String username);

}
