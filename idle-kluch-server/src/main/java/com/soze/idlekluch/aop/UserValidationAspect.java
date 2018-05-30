package com.soze.idlekluch.aop;

import com.soze.idlekluch.user.exception.NotAuthenticatedException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Aspect used to validate that there is a valid user in the current
 * SecurityContext.
 */
@Aspect
@Component
public class UserValidationAspect {

  @Pointcut("execution(* *(..)) && @annotation(com.soze.idlekluch.aop.annotations.Authorized))")
  public void authorizedMethodExecution() {}

  @Before("authorizedMethodExecution()")
  public void logAuthorizedMethod(final JoinPoint pjp) throws Throwable {
    final SecurityContext securityContext = SecurityContextHolder.getContext();

    if(securityContext.getAuthentication() == null) {
      throw new NotAuthenticatedException();
    }
  }


}
