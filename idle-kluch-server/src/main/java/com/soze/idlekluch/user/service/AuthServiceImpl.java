package com.soze.idlekluch.user.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.soze.idlekluch.aop.annotations.Profiled;
import com.soze.idlekluch.user.dto.ChangePasswordForm;
import com.soze.idlekluch.user.dto.Jwt;
import com.soze.idlekluch.user.dto.LoginForm;
import com.soze.idlekluch.user.entity.User;
import com.soze.idlekluch.user.exception.AuthUserDoesNotExistException;
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
  private Algorithm algorithm;

  @Autowired
  private JwtKeyProvider keyProvider;

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordHash passwordHash;

  @PostConstruct
  public void setup() {
    algorithm = Algorithm.HMAC256(keyProvider.getSecret());
  }

  @Override
  @Profiled
  public Jwt login(LoginForm loginForm) {
    validateLogin(loginForm);

    return new Jwt(
        JWT.create()
            .withIssuer(ISSUER)
            .withClaim(USER_NAME_CLAIM, loginForm.getUsername())
            .sign(algorithm)
    );
  }

  @Override
  public Jwt getToken(final String username) {
    return new Jwt(
        JWT.create()
            .withIssuer(ISSUER)
            .withClaim(USER_NAME_CLAIM, username)
            .sign(algorithm)
    );
  }

  //TODO refactor this, move somewhere else?
  private void validateLogin(LoginForm form) {
    Objects.requireNonNull(form);
    final User user = getUserByUsername(form.getUsername()).<AuthUserDoesNotExistException>orElseThrow(() -> {
      throw new AuthUserDoesNotExistException(form.getUsername());
    });

    final boolean passwordMatches = passwordHash.matches(form.getPassword(), user.getPasswordHash());
    if (!passwordMatches) {
      throw new InvalidPasswordException(form.getUsername());
    }
  }

  @Override
  public void logout(String token) {

  }

  @Override
  public boolean validateToken(String token) {
    Objects.requireNonNull(token);

    try {
      decodeToken(token);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  private DecodedJWT decodeToken(String token) {
    return JWT.require(algorithm).build().verify(token);
  }

  @Override
  public String getUsernameClaim(String token) {
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
  public void passwordChange(String username, ChangePasswordForm changePasswordForm) {
    validatePasswordChange(username, changePasswordForm);

    userService.changeUserPassword(username, passwordHash.hashWithSalt(changePasswordForm.getNewPassword()));
    changePasswordForm.reset();
  }

  private void validatePasswordChange(String username, ChangePasswordForm form) {
    Objects.requireNonNull(username);
    Objects.requireNonNull(form);

    User user = getUserByUsername(username).<AuthUserDoesNotExistException>orElseThrow(() -> {
      form.reset();
      throw new AuthUserDoesNotExistException(username);
    });

    if (Arrays.equals(form.getNewPassword(), form.getOldPassword())) {
      form.reset();
      throw new IdenticalPasswordChangeException();
    }

    boolean passwordMatches = passwordHash.matches(form.getOldPassword(), user.getPasswordHash());
    if (!passwordMatches) {
      form.reset();
      throw new InvalidPasswordException(username);
    }
  }

  private Optional<User> getUserByUsername(String username) {
    return userService.getUserByUsername(username);
  }

}
