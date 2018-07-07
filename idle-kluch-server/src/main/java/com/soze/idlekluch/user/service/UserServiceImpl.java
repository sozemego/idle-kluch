package com.soze.idlekluch.user.service;

import com.soze.idlekluch.core.aop.annotations.AuthLog;
import com.soze.idlekluch.core.aop.annotations.Profiled;
import com.soze.idlekluch.core.event.EventPublisher;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.user.dto.RegisterUserForm;
import com.soze.idlekluch.user.entity.User;
import com.soze.idlekluch.user.event.UserRemovedEvent;
import com.soze.idlekluch.user.exception.AuthUserDoesNotExistException;
import com.soze.idlekluch.user.exception.UserRegistrationException;
import com.soze.idlekluch.user.password.PasswordHash;
import com.soze.idlekluch.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements com.soze.idlekluch.user.service.UserService {

  private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

  private final UserRepository userRepository;
  private final PasswordHash passwordHash;
  private final EventPublisher eventPublisher;

  @Autowired
  public UserServiceImpl(final UserRepository userRepository,
                         final PasswordHash passwordHash,
                         final EventPublisher eventPublisher) {

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

    if (userRepository.usernameExists(username)) {
      throw new UserRegistrationException("username", username + " already exists.");
    }

    final char[] password = userForm.getPassword();
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

    return !userRepository.usernameExists(username);
  }

  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    return null;
  }
}
