package com.soze.idlekluch.interceptors;

import java.util.Objects;

public class RateLimitException extends RuntimeException {

  private final LimitedResource limitedResource;
  private final RateLimit rateLimit;
  private final long nextRequest; //time in seconds when a valid request can be made

  public RateLimitException(final LimitedResource limitedResource, final RateLimit rateLimit, final long nextRequest) {
    this.limitedResource = Objects.requireNonNull(limitedResource);
    this.rateLimit = Objects.requireNonNull(rateLimit);
    this.nextRequest = nextRequest;
  }

  public LimitedResource getLimitedResource() {
    return limitedResource;
  }

  public RateLimit getRateLimit() {
    return rateLimit;
  }

  public long getNextRequest() {
    return nextRequest;
  }
}
