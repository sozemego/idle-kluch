package com.soze.idlekluch.kingdom.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.soze.idlekluch.dto.Form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

public class RegisterKingdomForm extends Form implements Serializable {

  @NotNull
  @Size(min = 1, max = 32, message = "Kingdom name has to be between 1 and 32 characters")
  @Pattern(regexp = "[a-zA-Z0-9_-]+", message = "Kingdom name can only contain characters a-Z, 0-9 and underscores")
  private final String name;

  @JsonCreator
  public RegisterKingdomForm(@JsonProperty("name") final String name) {
    this.name = Objects.requireNonNull(name);
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "RegisterKingdomForm{" +
             "name='" + name + '\'' +
             '}';
  }
}
