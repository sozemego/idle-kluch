package com.soze.idlekluch.interceptors;

public interface RateLimitService {

  void applyFilter(RateLimit rateLimit, String user, LimitedResource limitedResource);

}
