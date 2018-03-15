package com.soze.idlekluch.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.security.Principal;

@Service
public class RateLimitInterceptor extends HandlerInterceptorAdapter {

  @Value("${IDLE_KLUCH_RATE_LIMIT_ENABLED}")
  private String rateLimitEnabled;

  @Autowired
  private RateLimitService rateLimitService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    if (!Boolean.valueOf(rateLimitEnabled)) {
      return true;
    }

    if(!(handler instanceof HandlerMethod)) {
      return true;
    }

    final Method method = ((HandlerMethod) handler).getMethod();

    final boolean isResourceAnnotationPresent = method.getDeclaringClass().isAnnotationPresent(RateLimited.class);
    final boolean isMethodAnnotationPresent = method.isAnnotationPresent(RateLimited.class);

    if (!isResourceAnnotationPresent && !isMethodAnnotationPresent) {
      return true;
    }

    RateLimited resourceAnnotation = isResourceAnnotationPresent ? method.getDeclaringClass().getAnnotation(RateLimited.class) : null;
    RateLimited methodAnnotation = isMethodAnnotationPresent ? method.getAnnotation(RateLimited.class) : null;

    //1. get the annotation to use
    //method values override type values
    RateLimited annotation = methodAnnotation != null ? methodAnnotation : resourceAnnotation;

    //2. create a value object for the resource
    LimitedResource limitedResource = new LimitedResource(
        method,
        request.getMethod(),
        request.getServletPath()
    );

    //3. find the user name or IP if anonymous
    Principal principal = SecurityContextHolder.getContext().getAuthentication();
    String user = principal != null ? principal.getName() : request.getRemoteAddr();

    //3. apply rate limiting filter for this method and user
    try {
      rateLimitService.applyFilter(getRateLimit(annotation), user, limitedResource);
    } catch (RateLimitException e) {
      return false;
    }

    return true;
  }

  private RateLimit getRateLimit(RateLimited rateLimited) {
    return new RateLimit(
        rateLimited.limit(),
        rateLimited.timeUnit(),
        rateLimited.timeUnits()
    );
  }


}
