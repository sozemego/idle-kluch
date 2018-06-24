package com.soze.idlekluch.core.utils;

public class MathUtils {

  public static boolean equals(final float a, final float b, final float epsilon) {
    return Math.abs(a - b) < epsilon;
  }

}
