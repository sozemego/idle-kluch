package com.soze.idlekluch.user.service;

import com.soze.idlekluch.aop.annotations.ValidForm;
import com.soze.idlekluch.user.dto.RegisterUserForm;
import com.soze.idlekluch.user.entity.User;
import com.soze.idlekluch.user.exception.AuthUserDoesNotExistException;
import com.soze.idlekluch.user.exception.UserRegistrationException;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

  @Deprecated
  List<User> getAllUsers();

  Optional<User> getUserById(EntityUUID userId);

  Optional<User> getUserByUsername(String username);

  /**
   * Will either return the user or throw {@link AuthUserDoesNotExistException}
   * @param username
   * @return
   */
  User getUserOrThrow(String username);

  /**
   * Attempts to register a new user.
   * Username case is ignored.
   * Must clear password field from RegisterUserForm using the reset() method.
   * Username cannot be longer than 25 characters.
   *
   * @throws UserRegistrationException if there is a problem with the registration
   */
  void addUser(@ValidForm RegisterUserForm userForm);

  void changeUserPassword(String username, String hash);

  /**
   * Attempts to delete a given user.
   * Deleted users cannot log in, but their names cannot be taken again either.
   */
  void deleteUser(String username);

  /**
   * Checks if given username is available for registration.
   * Returns true if it is available, false otherwise.
   * Also returns false for all illegal usernames.
   */
  boolean isAvailableForRegistration(String username);

}
