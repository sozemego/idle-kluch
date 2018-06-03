package com.soze.idlekluch.user.repository;


import com.soze.idlekluch.user.entity.User;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

  /**
   * Returns a list of all users. This is for development only and will be removed in the future
   */
  @Deprecated
  List<User> getAllUsers();

  Optional<User> getUserById(EntityUUID userId);

  /**
   * Attempts to find a user by it's username.
   * This search is case insensitive.
   */
  Optional<User> getUserByUsername(String username);

  /**
   * Returns true if user with given username exists. False otherwise.
   * If a user existed with this name, but was deleted, this method will also return true.
   * Case insensitive.
   */
  boolean usernameExists(String username);

  /**
   * Attempts to add a given user. Will throw exceptions if the user exists already (same user name or id).
   */
  void addUser(User user);

  /**
   * Updates a user entity.
   */
  void updateUser(User user);

  /**
   * Marks given user as deleted.
   */
  void deleteUser(String username);
}
