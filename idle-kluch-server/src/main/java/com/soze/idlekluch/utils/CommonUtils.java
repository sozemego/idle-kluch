package com.soze.idlekluch.utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Miscellaneous utils which do not fit in any category so far.
 */
public class CommonUtils {

  private static final String ALPHANUMERIC_CHARACTERS = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890";

  public static String generateRandomString(int length) {
    if (length < 0) {
      throw new IllegalArgumentException("Length cannot be negative.");
    }

    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(generateRandomCharacterAlphanumeric());
    }

    return sb.toString();
  }

  public static Character generateRandomCharacterAlphanumeric() {
    return ALPHANUMERIC_CHARACTERS.charAt(randomNumber(0, ALPHANUMERIC_CHARACTERS.length()));
  }

  /**
   * Generates a random number between min and max. Min is inclusive, max is inclusive.
   *
   * @param min min
   * @param max max
   * @return random number between min and max
   */
  public static int randomNumber(int min, int max) {
    if (max < min) {
      throw new IllegalArgumentException("Max cannot be less than min.");
    }

    return ThreadLocalRandom.current().nextInt(min, max + 1);
  }

  public static int parseInt(String str, int defaultValue) {
    return isInteger(str) ? Integer.parseInt(str) : defaultValue;
  }

  /**
   * Returns true if the given str represents an integer.
   * False otherwise
   *
   * @param str input string
   * @return true if given string can be parsed into an integer. False otherwise
   */
  public static boolean isInteger(String str) {
    char[] chars = str.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      if (!Character.isDigit(chars[i])) {
        return false;
      }
    }
    return true;
  }

}
