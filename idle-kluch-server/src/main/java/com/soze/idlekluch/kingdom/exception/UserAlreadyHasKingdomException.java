package com.soze.idlekluch.kingdom.exception;


import java.util.Objects;

public class UserAlreadyHasKingdomException extends RuntimeException {

  private final String username;
  private final String kingdomName;

  public UserAlreadyHasKingdomException(final String username, final String kingdomName) {
    this.username = Objects.requireNonNull(username);
    this.kingdomName = Objects.requireNonNull(kingdomName);
  }

  public String getUsername() {
    return username;
  }

  public String getKingdomName() {
    return kingdomName;
  }
}
