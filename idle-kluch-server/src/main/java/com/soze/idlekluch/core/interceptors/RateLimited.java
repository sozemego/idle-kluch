package com.soze.idlekluch.core.interceptors;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Rate-limiting annotation for filters.
 * Defaults to 100 requests per 10 minutes (per end-point or per resource class).
 * Method level values overrides type level values.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, METHOD})
public @interface RateLimited {

  int limit() default 100;

  TimeUnit timeUnit() default TimeUnit.MINUTES;

  int timeUnits() default 10;

}
