package com.soze.idlekluch.interceptors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimitServiceImpl implements RateLimitService {

  private static final Logger LOG = LoggerFactory.getLogger(RateLimitServiceImpl.class);

  private final Map<LimitedResource, Multimap<String, Long>> requestHistory = new ConcurrentHashMap<>();

  @Override
  public void applyFilter(final RateLimit rateLimit, final String user, final LimitedResource limitedResource) {
    Objects.requireNonNull(rateLimit);
    Objects.requireNonNull(user);
    Objects.requireNonNull(limitedResource);

    final Multimap<String, Long> userRequests = requestHistory.compute(limitedResource, (k, v) -> {
      if (v == null) {
        v = Multimaps.synchronizedMultimap(HashMultimap.create());
      }
      return v;
    });

    final int limit = rateLimit.getLimit();
    final TimeUnit timeUnit = rateLimit.getTimeUnit();
    final int timeUnits = rateLimit.getTimeUnits();

    final List<Long> userTimestamps = new LinkedList<>(userRequests.get(user));


    //1. remove all previous requests older than timeUnit * timeUnits
    //   to do that, we need to find the timestamp before which we will remove previous requests
    final long now = Instant.now().toEpochMilli();
    userTimestamps.add(now);
    final long rateLimitWindow = TimeUnit.MILLISECONDS.convert(timeUnits, timeUnit);
    final long removeBefore = now - rateLimitWindow;

    userTimestamps.removeIf(timestamp -> timestamp < removeBefore);

    //2. get count of requests that were left
    final int requests = userTimestamps.size();

    //3. if everything is ok, don't throw exception

    //4. if over limit, throw exception
    if (requests > limit) {
      //4a. calculate when next request will be allowed to be fired
      //    you can only be over limit by 1
      //    this is why we get the request at index 0
      final long timeNextRequestVanishes = userTimestamps.get(0) + rateLimitWindow - now;

      throw new RateLimitException(limitedResource, rateLimit, timeNextRequestVanishes / 1000);
    }

    //5. add this request to the history
    userRequests.replaceValues(user, userTimestamps);
  }

}
