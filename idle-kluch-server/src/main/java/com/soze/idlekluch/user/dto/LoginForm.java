package com.soze.idlekluch.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Contains username and password. Remember to call the reset() method after use.
 */
public class LoginForm {

  private String username;
  private char[] password;

  @JsonCreator
  public LoginForm(
    @JsonProperty("username") final String username,
    @JsonProperty("password") final char[] password
  ) {
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
    this.password = null;
  }

  @Override
  public String toString() {
    return "LoginForm{" +
             "username='" + username + '\'' +
             '}';
  }
}
