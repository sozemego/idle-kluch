package com.soze.idlekluch.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Objects;

@Service
public class RateLimitInterceptor extends HandlerInterceptorAdapter {

  @Value("${IDLE_KLUCH_RATE_LIMIT_ENABLED}")
  private String rateLimitEnabledString;

  private boolean rateLimitEnabled;

  private final RateLimitService rateLimitService;

  @Autowired
  public RateLimitInterceptor(final RateLimitService rateLimitService) {
    this.rateLimitService = Objects.requireNonNull(rateLimitService);
  }

  @PostConstruct
  public void postConstruct() {
    rateLimitEnabled = Boolean.valueOf(rateLimitEnabledString);
    System.out.println("Rate limiting is enabled: " + rateLimitEnabled);
  }

  @Override
  public boolean preHandle(final HttpServletRequest request,
                           final HttpServletResponse response,
                           final Object handler) throws Exception {
    if (!rateLimitEnabled) {
      return true;
    }

    if (!(handler instanceof HandlerMethod)) {
      return true;
    }

    final Method method = ((HandlerMethod) handler).getMethod();

    final boolean isResourceAnnotationPresent = method.getDeclaringClass().isAnnotationPresent(RateLimited.class);
    final boolean isMethodAnnotationPresent = method.isAnnotationPresent(RateLimited.class);

    if (!isResourceAnnotationPresent && !isMethodAnnotationPresent) {
      return true;
    }

    final RateLimited resourceAnnotation = isResourceAnnotationPresent ? method.getDeclaringClass().getAnnotation(RateLimited.class) : null;
    final RateLimited methodAnnotation = isMethodAnnotationPresent ? method.getAnnotation(RateLimited.class) : null;

    //1. get the annotation to use
    //method values override type values
    final RateLimited annotation = methodAnnotation != null ? methodAnnotation : resourceAnnotation;

    //2. create a value object for the resource
    final LimitedResource limitedResource = new LimitedResource(
      method,
      request.getMethod(),
      request.getServletPath()
    );

    //3. find the user name or IP if anonymous
    final Principal principal = SecurityContextHolder.getContext().getAuthentication();
    final String user = principal != null ? principal.getName() : request.getRemoteAddr();

    //3. apply rate limiting filter for this method and user
    rateLimitService.applyFilter(getRateLimit(annotation), user, limitedResource);

    return true;
  }

  private RateLimit getRateLimit(final RateLimited rateLimited) {
    return new RateLimit(
      rateLimited.limit(),
      rateLimited.timeUnit(),
      rateLimited.timeUnits()
    );
  }

}
