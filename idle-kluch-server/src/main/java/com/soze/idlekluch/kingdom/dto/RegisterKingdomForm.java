package com.soze.idlekluch.kingdom.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

public class RegisterKingdomForm implements Serializable {

  private final String name;

  @JsonCreator
  public RegisterKingdomForm(@JsonProperty("name") final String name) {
    this.name = Objects.requireNonNull(name);
  }

  public String getName() {
    return name;
  }

}
