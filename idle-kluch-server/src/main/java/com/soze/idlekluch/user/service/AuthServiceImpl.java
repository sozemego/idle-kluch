package com.soze.idlekluch.user.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.soze.idlekluch.aop.annotations.AuthLog;
import com.soze.idlekluch.aop.annotations.Profiled;
import com.soze.idlekluch.user.dto.ChangePasswordForm;
import com.soze.idlekluch.user.dto.Jwt;
import com.soze.idlekluch.user.dto.LoginForm;
import com.soze.idlekluch.user.entity.User;
import com.soze.idlekluch.user.exception.IdenticalPasswordChangeException;
import com.soze.idlekluch.user.exception.InvalidPasswordException;
import com.soze.idlekluch.user.password.PasswordHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

  private static final String ISSUER = "idle_kluch";
  private static final String USER_NAME_CLAIM = "username";

  private final JwtKeyProvider keyProvider;
  private final UserService userService;
  private final PasswordHash passwordHash;

  private final Algorithm algorithm;

  public AuthServiceImpl(final JwtKeyProvider keyProvider,
                         final UserService userService,
                         final PasswordHash passwordHash) {
    this.keyProvider = Objects.requireNonNull(keyProvider);
    this.userService = Objects.requireNonNull(userService);
    this.passwordHash = Objects.requireNonNull(passwordHash);
    this.algorithm = Algorithm.HMAC256(keyProvider.getSecret());
  }

  @Override
  @Profiled
  @AuthLog
  public Jwt login(final LoginForm loginForm) {
    validateLogin(loginForm);

    return new Jwt(
        JWT.create()
            .withIssuer(ISSUER)
            .withClaim(USER_NAME_CLAIM, loginForm.getUsername())
            .sign(algorithm)
    );
  }

  @Override
  @AuthLog
  public Jwt getToken(final String username) {
    return new Jwt(
        JWT.create()
            .withIssuer(ISSUER)
            .withClaim(USER_NAME_CLAIM, username)
            .sign(algorithm)
    );
  }

  //TODO refactor this, move somewhere else?
  private void validateLogin(final LoginForm form) {
    Objects.requireNonNull(form);

    final User user = getUserByUsername(form.getUsername()).get();

    final boolean passwordMatches = passwordHash.matches(form.getPassword(), user.getPasswordHash());
    if (!passwordMatches) {
      throw new InvalidPasswordException(form.getUsername());
    }
  }

  @Override
  public void logout(final String token) {

  }

  @Override
  public boolean validateToken(final String token) {
    Objects.requireNonNull(token);

    try {
      decodeToken(token);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  private DecodedJWT decodeToken(final String token) {
    return JWT.require(algorithm).build().verify(token);
  }

  @Override
  public String getUsernameClaim(final String token) {
    DecodedJWT decodedJWT = decodeToken(token);
    Claim claim = decodedJWT.getClaim(USER_NAME_CLAIM);
    if (claim.isNull()) {
      //TODO make own exception
      throw new IllegalArgumentException("NO USERNAME CLAIM");
    }
    return claim.asString();
  }

  @Override
  @Profiled
  @AuthLog
  public void passwordChange(final String username, final ChangePasswordForm changePasswordForm) {
    validatePasswordChange(username, changePasswordForm);

    userService.changeUserPassword(username, passwordHash.hashWithSalt(changePasswordForm.getNewPassword()));
    changePasswordForm.reset();
  }

  private void validatePasswordChange(final String username, final ChangePasswordForm form) {
    Objects.requireNonNull(username);
    Objects.requireNonNull(form);

    final User user = getUserByUsername(username).get();

    try {

      if (Arrays.equals(form.getNewPassword(), form.getOldPassword())) {
        throw new IdenticalPasswordChangeException();
      }

      boolean passwordMatches = passwordHash.matches(form.getOldPassword(), user.getPasswordHash());
      if (!passwordMatches) {
        throw new InvalidPasswordException(username);
      }

    } finally {
      form.reset();
    }

  }

  private Optional<User> getUserByUsername(final String username) {
    return userService.getUserByUsername(username);
  }

}
