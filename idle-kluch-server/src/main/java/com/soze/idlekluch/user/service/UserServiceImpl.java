package com.soze.idlekluch.user.service;

import com.soze.idlekluch.aop.annotations.Authorized;
import com.soze.idlekluch.aop.annotations.Profiled;
import com.soze.idlekluch.user.dto.RegisterUserForm;
import com.soze.idlekluch.user.entity.User;
import com.soze.idlekluch.user.exception.AuthUserDoesNotExistException;
import com.soze.idlekluch.user.exception.UserRegistrationException;
import com.soze.idlekluch.user.password.PasswordHash;
import com.soze.idlekluch.user.repository.UserRepository;
import com.soze.idlekluch.utils.jpa.EntityUUID;
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
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

  private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
  private static final Pattern USERNAME_VALIDATOR = Pattern.compile("[a-zA-Z0-9_-]+");
  private static final int MAX_USERNAME_LENGTH = 38;
  private static final int MAX_PASSWORD_LENGTH = 128;

  private final UserRepository userRepository;
  private final PasswordHash passwordHash;

  @Autowired
  public UserServiceImpl(final UserRepository userRepository, final PasswordHash passwordHash) {
    this.userRepository = Objects.requireNonNull(userRepository);
    this.passwordHash = Objects.requireNonNull(passwordHash);
  }

  @Deprecated
  public List<User> getAllUsers() {
    return userRepository.getAllUsers();
  }

  public Optional<User> getUserById(EntityUUID userId) {
    return userRepository.getUserById(userId);
  }

  public Optional<User> getUserByUsername(String username) {
    return userRepository.getUserByUsername(username);
  }

  @Override
  @Profiled
  public void addUser(RegisterUserForm userForm) {
    Objects.requireNonNull(userForm);

    String username = userForm.getUsername();
    LOG.info("Attempting to create user with username [{}]", username);
    validateUsername(username);

    char[] password = userForm.getPassword();
    if (password.length == 0) {
      LOG.info("Username [{}] input an empty password.", username);
      throw new UserRegistrationException("password", "Password cannot be empty.");
    }

    if (password.length > MAX_PASSWORD_LENGTH) {
      LOG.info("Username [{}] input too long password.", username);
      throw new UserRegistrationException("password", "Password cannot be longer than " + MAX_PASSWORD_LENGTH);
    }

    if (userRepository.usernameExists(username)) {
      LOG.info("Username [{}] already exists!", username);
      throw new UserRegistrationException("username", username + " already exists.");
    }

    LOG.info("[{}] is free, registering user. ", username);

    String hashedPassword = passwordHash.hashWithSalt(password);
    userForm.reset();

    User user = new User();
    user.setUsername(userForm.getUsername());
    user.setPasswordHash(hashedPassword);
    user.setUserId(EntityUUID.randomId());
    user.setCreatedAt(OffsetDateTime.now());

    userRepository.addUser(user);

    LOG.info("Registered user [{}]", username);
  }

  private void validateUsername(String username) {
    Objects.requireNonNull(username);

    if (!USERNAME_VALIDATOR.matcher(username).matches()) {
      LOG.info("[{}] is not a valid username!", username);
      throw new UserRegistrationException("username", "Username can only contain letters, numbers, '-' and '_'");
    }

    if (username.length() > MAX_USERNAME_LENGTH) {
      LOG.info("Username [{}] was too long!", username);
      throw new UserRegistrationException("username", "Username cannot be longer than " + MAX_USERNAME_LENGTH);
    }

  }

  @Override
  @Authorized
  public void changeUserPassword(String username, String hash) {
    Objects.requireNonNull(username);
    Objects.requireNonNull(hash);

    User user = getUserByUsername(username).orElseThrow(() -> new AuthUserDoesNotExistException(username));
    user.setPasswordHash(hash);
    userRepository.updateUser(user);
  }

  @Override
  public void deleteUser(String username) {
    Objects.requireNonNull(username);

    userRepository.deleteUser(username);
  }

  @Override
  @Profiled
  public boolean isAvailableForRegistration(String username) {
    try {
      validateUsername(username);
    } catch (UserRegistrationException e) {
      return false;
    }
    return !userRepository.usernameExists(username);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return null;
  }
}
