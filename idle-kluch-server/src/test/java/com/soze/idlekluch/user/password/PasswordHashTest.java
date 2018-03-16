package com.soze.idlekluch.user.password;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PasswordHashTest {

  private PasswordHash passwordHash;

  @Before
  public void setup() {
    passwordHash = new PasswordHash();
  }

  @Test
  public void testValidPassword() throws Exception {
    char[] password = "cool password".toCharArray();

    String hash = passwordHash.hashWithSalt(password);
    assertTrue(hash != null);
    assertFalse(hash.isEmpty());
  }

  @Test
  public void testEmptyPassword() throws Exception {
    char[] password = "".toCharArray();

    String hash = passwordHash.hashWithSalt(password);
    assertTrue(hash != null);
    assertFalse(hash.isEmpty());
  }

  @Test
  public void testVeryLongPassword() throws Exception {
    char[] password = Stream.generate(() -> "abcd")
      .limit(500)
      .reduce("", (a, b) -> a + b)
      .toCharArray();

    String hash = passwordHash.hashWithSalt(password);
    assertTrue(hash != null);
    assertFalse(hash.isEmpty());
  }

  @Test
  public void testPasswordHashMatches() throws Exception {
    char[] password = "cool password".toCharArray();

    String hash = passwordHash.hashWithSalt(password);
    assertTrue(passwordHash.matches(password, hash));
  }

  @Test
  public void testEmptyPasswordHashMatches() throws Exception {
    char[] password = "".toCharArray();

    String hash = passwordHash.hashWithSalt(password);
    assertTrue(passwordHash.matches(password, hash));
  }

  @Test
  public void testLongPasswordHashMatches() throws Exception {
    char[] password = Stream.generate(() -> "abcd")
      .limit(500)
      .reduce("", (a, b) -> a + b)
      .toCharArray();

    String hash = passwordHash.hashWithSalt(password);
    assertTrue(passwordHash.matches(password, hash));
  }

  @Test
  public void testMultipleTimesPasswordHashIsDifferent() throws Exception {
    char[] password = "cool password, bro".toCharArray();
    int hashCount = 5;

    Set<String> hashes = new HashSet<>();
    for (int i = 0; i < hashCount; i++) {
      hashes.add(passwordHash.hashWithSalt(password));
    }

    assertTrue(hashes.size() == hashCount);
  }

  @Test
  public void testMultipleTimesPasswordHashAllMatch() throws Exception {
    char[] password = "abce".toCharArray();
    int hashCount = 5;

    Set<String> hashes = new HashSet<>();
    for (int i = 0; i < hashCount; i++) {
      hashes.add(passwordHash.hashWithSalt(password));
    }

    hashes.forEach(hash -> assertTrue(passwordHash.matches(password, hash)));
  }

  @Test
  public void testDifferentHashDoesNotMatch() throws Exception {
    char[] password1 = "cool password, bro".toCharArray();
    char[] password2 = "abce!#chars".toCharArray();

    String hash1 = passwordHash.hashWithSalt(password1);
    String hash2 = passwordHash.hashWithSalt(password2);
    assertTrue(passwordHash.matches(password1, hash1));
    assertTrue(passwordHash.matches(password2, hash2));
    assertFalse(passwordHash.matches(password1, hash2));
    assertFalse(passwordHash.matches(password2, hash1));
  }


}