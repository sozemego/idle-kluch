package com.soze.idlekluch.kingdom.exception;

import java.util.Objects;

public class UserDoesNotHaveKingdomException extends RuntimeException {

  private final String username;

  public UserDoesNotHaveKingdomException(final String username) {
    this.username = Objects.requireNonNull(username);
  }

  public String getUsername() {
    return username;
  }

}
