package com.soze.idlekluch.utils;

import java.util.List;
import java.util.Optional;
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

    final StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(generateRandomCharacterAlphanumeric());
    }

    return sb.toString();
  }

  public static Character generateRandomCharacterAlphanumeric() {
    return ALPHANUMERIC_CHARACTERS.charAt(randomNumber(0, ALPHANUMERIC_CHARACTERS.length() - 1));
  }

  /**
   * Generates a random number between min and max. Min is inclusive, max is inclusive.
   */
  public static int randomNumber(final int min, final int max) {
    if (max < min) {
      throw new IllegalArgumentException("Max cannot be less than min.");
    }

    return ThreadLocalRandom.current().nextInt(min, max + 1);
  }

  /**
   * Generates a random number between min and max. Min is inclusive, max is exclusive.
   */
  public static double randomNumber(final double min, final double max) {
    if (max < min) {
      throw new IllegalArgumentException("Max cannot be less than min.");
    }
    return ThreadLocalRandom.current().nextDouble(min, max);
  }

  public static int parseInt(final String str, final int defaultValue) {
    return isInteger(str) ? Integer.parseInt(str) : defaultValue;
  }

  /**
   * Returns true if the given str represents an integer.
   * False otherwise
   *
   * @param str input string
   * @return true if given string can be parsed into an integer. False otherwise
   */
  public static boolean isInteger(final String str) {
    final char[] chars = str.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      if (!Character.isDigit(chars[i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * Finds a random element from the list.
   */
  public static <T> Optional<T> getRandomElement(final List<T> list) {
    final int index = randomNumber(0, list.size() - 1);
    if(index >= list.size()) {
      return Optional.empty();
    }
    return Optional.of(list.get(index));
  }

}
