package com.soze.idlekluch.user.event;

import java.util.Objects;

public class UserRemovedEvent {

  private final String username;

  public UserRemovedEvent(final String username) {
    this.username = Objects.requireNonNull(username);
  }

  public String getUsername() {
    return username;
  }
}
