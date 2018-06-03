package com.soze.idlekluch.core.interceptors;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RateLimit {

  private final int limit;
  private final TimeUnit timeUnit;
  private final int timeUnits;

  public RateLimit(final int limit, final TimeUnit timeUnit, final int timeUnits) {
    this.limit = limit;
    this.timeUnit = Objects.requireNonNull(timeUnit);
    this.timeUnits = timeUnits;
  }

  public int getLimit() {
    return limit;
  }

  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  public int getTimeUnits() {
    return timeUnits;
  }

  @Override
  public String toString() {
    return "RateLimit{" +
      "limit=" + limit +
      ", timeUnit=" + timeUnit +
      ", timeUnits=" + timeUnits +
      '}';
  }
}
