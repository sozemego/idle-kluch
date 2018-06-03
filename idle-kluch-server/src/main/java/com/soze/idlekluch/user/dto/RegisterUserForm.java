package com.soze.idlekluch.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.soze.idlekluch.core.dto.Form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Objects;

public class RegisterUserForm extends Form {

  private static final int MAX_USERNAME_LENGTH = 38;
  private static final int MAX_PASSWORD_LENGTH = 128;

  @NotNull
  @Size(
    min = 1,
    max = MAX_USERNAME_LENGTH,
    message = "Username length has to be between 1 and " + MAX_USERNAME_LENGTH
  )
  @Pattern(regexp = "[a-zA-Z0-9_-]+", message = "Username can only contain letters, numbers, '-' and '_'")
  private String username;

  @Size(min = 1, max = MAX_PASSWORD_LENGTH)
  private final char[] password;

  @JsonCreator
  public RegisterUserForm(@JsonProperty("username") final String username, @JsonProperty("password") final char[] password) {
    this.username = Objects.requireNonNull(username);
    this.password = Objects.requireNonNull(password);
  }

  public String getUsername() {
    return username;
  }

  public char[] getPassword() {
    return password;
  }

  public void reset() {
    for (int i = 0; i < password.length; i++) {
      password[i] = 0; //attempts to clear password from memory
    }
  }

  @Override
  public String toString() {
    return "RegisterUserForm{" +
      "username='" + username + '\'' +
      '}';
  }
}
