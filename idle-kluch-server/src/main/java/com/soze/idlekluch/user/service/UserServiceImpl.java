package com.soze.idlekluch.user.service;

import com.soze.idlekluch.aop.annotations.AuthLog;
import com.soze.idlekluch.aop.annotations.Profiled;
import com.soze.idlekluch.user.dto.RegisterUserForm;
import com.soze.idlekluch.user.entity.User;
import com.soze.idlekluch.user.event.UserRemovedEvent;
import com.soze.idlekluch.user.exception.AuthUserDoesNotExistException;
import com.soze.idlekluch.user.exception.UserRegistrationException;
import com.soze.idlekluch.user.password.PasswordHash;
import com.soze.idlekluch.user.repository.UserRepository;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements com.soze.idlekluch.user.service.UserService {

  private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
  private static final Pattern USERNAME_VALIDATOR = Pattern.compile("[a-zA-Z0-9_-]+");
  private static final int MAX_USERNAME_LENGTH = 38;
  private static final int MAX_PASSWORD_LENGTH = 128;

  private final UserRepository userRepository;
  private final PasswordHash passwordHash;
  private final ApplicationEventPublisher eventPublisher;

  @Autowired
  public UserServiceImpl(final UserRepository userRepository,
                         final PasswordHash passwordHash,
                         final ApplicationEventPublisher eventPublisher) {

    this.userRepository = Objects.requireNonNull(userRepository);
    this.passwordHash = Objects.requireNonNull(passwordHash);
    this.eventPublisher = Objects.requireNonNull(eventPublisher);
  }

  @Deprecated
  public List<User> getAllUsers() {
    return userRepository.getAllUsers();
  }

  public Optional<User> getUserById(final EntityUUID userId) {
    return userRepository.getUserById(userId);
  }

  public Optional<User> getUserByUsername(final String username) {
    return userRepository.getUserByUsername(username);
  }

  @Override
  public User getUserOrThrow(final String username) {
    return getUserByUsername(username)
             .orElseThrow(() -> new AuthUserDoesNotExistException(username));
  }

  @Override
  @Profiled
  public void addUser(final RegisterUserForm userForm) {
    Objects.requireNonNull(userForm);

    final String username = userForm.getUsername();
    LOG.info("Attempting to create user with username [{}]", username);
    validateUsername(username);

    if (userRepository.usernameExists(username)) {
      throw new UserRegistrationException("username", username + " already exists.");
    }

    final char[] password = userForm.getPassword();
    validatePassword(password);
    final String hashedPassword = passwordHash.hashWithSalt(password);
    userForm.reset();

    final User user = new User();
    user.setUsername(userForm.getUsername());
    user.setPasswordHash(hashedPassword);
    user.setUserId(EntityUUID.randomId());
    user.setCreatedAt(OffsetDateTime.now());

    userRepository.addUser(user);

    LOG.info("Registered user [{}]", username);
  }

  private void validateUsername(final String username) {
    Objects.requireNonNull(username);

    if (!USERNAME_VALIDATOR.matcher(username).matches()) {
      throw new UserRegistrationException("username", "Username can only contain letters, numbers, '-' and '_'");
    }

    if (username.length() > MAX_USERNAME_LENGTH) {
      throw new UserRegistrationException("username", "Username cannot be longer than " + MAX_USERNAME_LENGTH);
    }
  }

  private void validatePassword(final char[] password) {
    if (password.length == 0) {
      throw new UserRegistrationException("password", "Password cannot be empty.");
    }

    if (password.length > MAX_PASSWORD_LENGTH) {
      throw new UserRegistrationException("password", "Password cannot be longer than " + MAX_PASSWORD_LENGTH);
    }
  }

  @Override
  @AuthLog
  public void changeUserPassword(final String username, final String hash) {
    Objects.requireNonNull(username);
    Objects.requireNonNull(hash);

    final User user = getUserByUsername(username).get();
    user.setPasswordHash(hash);
    userRepository.updateUser(user);
  }

  @Override
  @AuthLog
  public void deleteUser(final String username) {
    Objects.requireNonNull(username);
    userRepository.deleteUser(username);
    eventPublisher.publishEvent(new UserRemovedEvent(username));
  }

  @Override
  @Profiled
  public boolean isAvailableForRegistration(final String username) {
    Objects.requireNonNull(username);
    try {
      validateUsername(username);
    } catch (UserRegistrationException e) {
      return false;
    }
    return !userRepository.usernameExists(username);
  }

  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    return null;
  }
}
