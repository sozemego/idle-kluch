package com.soze.idlekluch.user.password;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class PasswordHash {

  /**
   * Hashes a given password with a salt.
   *
   * @param password password to hash
   * @return salted hash
   */
  public String hashWithSalt(char[] password) {
    return BCrypt.hashpw(String.copyValueOf(password), BCrypt.gensalt(10));
  }

  public boolean matches(char[] password, String hash) {
    return BCrypt.checkpw(String.copyValueOf(password), hash);
  }

}
