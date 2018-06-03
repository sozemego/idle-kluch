package com.soze.idlekluch.core.interceptors;

public interface RateLimitService {

  void applyFilter(RateLimit rateLimit, String user, LimitedResource limitedResource);

}
