package com.soze.idlekluch.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class SimpleUserDto {

  private String username;

  @JsonCreator
  public SimpleUserDto(@JsonProperty("username") String username) {
    this.username = Objects.requireNonNull(username);
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
