package com.soze.idlekluch.interceptors;

public interface RateLimitService {

  void applyFilter(final RateLimit rateLimit, final String user, final LimitedResource limitedResource);

}
