package com.soze.idlekluch.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.soze.idlekluch.core.dto.Form;

import javax.validation.constraints.Size;
import java.util.Objects;

public class ChangePasswordForm extends Form {

  private static final int MAX_PASSWORD_LENGTH = 128;

  @Size(min = 1, max = MAX_PASSWORD_LENGTH, message = "Password length must be between 1 and " + MAX_PASSWORD_LENGTH)
  private final char[] oldPassword;

  @Size(min = 1, max = MAX_PASSWORD_LENGTH, message = "Password length must be between 1 and " + MAX_PASSWORD_LENGTH)
  private final char[] newPassword;

  @JsonCreator
  public ChangePasswordForm(@JsonProperty("oldPassword") char[] oldPassword,
                            @JsonProperty("newPassword") char[] newPassword) {
    this.oldPassword = Objects.requireNonNull(oldPassword);
    this.newPassword = Objects.requireNonNull(newPassword);
  }

  public char[] getOldPassword() {
    return oldPassword;
  }

  public char[] getNewPassword() {
    return newPassword;
  }

  public void reset() {
    for (int i = 0; i < oldPassword.length; i++) {
      oldPassword[i] = 0;
    }
    for (int i = 0; i < newPassword.length; i++) {
      newPassword[i] = 0;
    }
  }
}
