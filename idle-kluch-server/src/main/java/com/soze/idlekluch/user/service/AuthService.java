package com.soze.idlekluch.user.service;

import com.soze.idlekluch.aop.annotations.ValidForm;
import com.soze.idlekluch.user.dto.ChangePasswordForm;
import com.soze.idlekluch.user.dto.Jwt;
import com.soze.idlekluch.user.dto.LoginForm;
import com.soze.idlekluch.user.exception.AuthUserDoesNotExistException;
import com.soze.idlekluch.user.exception.InvalidPasswordException;


public interface AuthService {

  /**
   * Attempts to authenticate the user and returns a token
   * used to identify the user.
   *
   * @throws NullPointerException          if loginForm is null, or username in loginForm is null
   * @throws AuthUserDoesNotExistException if user with username specified in loginForm does not exist
   * @throws InvalidPasswordException      if password given by user is invalid
   */
  Jwt login(final LoginForm loginForm);

  Jwt getToken(final String username);

  /**
   * Given a token, logs out the user. For now, the method will only
   * log that user logged out. Client-side, cookie will be removed.
   */
  void logout(final String token);

  /**
   * Validates a given JWT. Returns true if it's valid, false otherwise.
   * This method does not check any claims, just checks whether the token
   * was tampered with.
   */
  boolean validateToken(final String token);

  /**
   * Returns username claim from a given token.
   */
  String getUsernameClaim(final String token);

  /**
   * Attempts to change user's password.
   * If the old password does not match, throws InvalidPasswordException
   *
   * @throws NullPointerException     if changePasswordForm is null
   * @throws InvalidPasswordException if old password given does not match current password
   */
  void passwordChange(final String username, @ValidForm final ChangePasswordForm changePasswordForm);

}
