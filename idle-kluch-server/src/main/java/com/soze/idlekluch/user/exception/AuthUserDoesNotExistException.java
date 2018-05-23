package com.soze.idlekluch.user.exception;

import java.util.Objects;

public class AuthUserDoesNotExistException extends RuntimeException {

  private final String username;

  public AuthUserDoesNotExistException(String username) {
    this.username = Objects.requireNonNull(username);
  }

  public String getUsername() {
    return username;
  }

  @Override
  public String getMessage() {
    return "User " + username + " does not exist";
  }
}
