package com.soze.idlekluch.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

  private static final Logger LOG = LoggerFactory.getLogger(LoggingAspect.class);

  @Pointcut("execution(* *(..)) && @annotation(com.soze.idlekluch.aop.annotations.Authorized))")
  public void authorizedMethodExecution() {}

  @Before("authorizedMethodExecution()")
  public void logAuthorizedMethod(final JoinPoint pjp) throws Throwable {
    final String username = SecurityContextHolder.getContext().getAuthentication().getName();
    LOG.info(
      "AUTH: [{}] [{}] [{}]",
      username,
      pjp.getSignature().toShortString(),
      pjp.getArgs()
    );
  }

}
