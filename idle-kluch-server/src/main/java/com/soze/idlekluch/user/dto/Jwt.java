package com.soze.idlekluch.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Encapsulates a JWT.
 */
public class Jwt {

  private final String jwt;

  @JsonCreator
  public Jwt(@JsonProperty("jwt") String jwt) {
    this.jwt = Objects.requireNonNull(jwt);
  }

  public String getJwt() {
    return jwt;
  }
}
