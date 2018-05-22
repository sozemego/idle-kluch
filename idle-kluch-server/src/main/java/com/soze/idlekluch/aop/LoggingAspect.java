package com.soze.idlekluch.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

  private static final Logger LOG = LoggerFactory.getLogger(LoggingAspect.class);

  private static final Marker AUTHORIZED_MARKER = MarkerFactory.getMarker("AUTHORIZED");

  @Pointcut("execution(* *(..)) && @annotation(com.soze.idlekluch.aop.annotations.Authorized))")
  public void authorizedMethodExecution() {}

  @Before("authorizedMethodExecution()")
  public void logAuthorizedMethod(final JoinPoint pjp) throws Throwable {
    final String username = SecurityContextHolder.getContext().getAuthentication().getName();
    LOG.info(
      AUTHORIZED_MARKER,
      "AUTH BEFORE: [{}] [{}] [{}]",
      username,
      pjp.getSignature().toShortString(),
      pjp.getArgs()
    );
  }

  @AfterReturning("authorizedMethodExecution()")
  public void afterAuthorizedMethodReturning(final JoinPoint pjp) throws Throwable {
    final String username = SecurityContextHolder.getContext().getAuthentication().getName();
    LOG.info(
      AUTHORIZED_MARKER,
      "AUTH AFTER: [{}] [{}]",
      username,
      pjp.getSignature().toShortString()
    );
  }

  @AfterThrowing(pointcut = "authorizedMethodExecution()", throwing = "ex")
  public void afterAuthorizedMethodThrowing(final JoinPoint pjp, final Throwable ex) throws Throwable {
    final String username = SecurityContextHolder.getContext().getAuthentication().getName();
    LOG.info(
      AUTHORIZED_MARKER,
      "AUTH THROWN: [{}] [{}] [{}]",
      username,
      pjp.getSignature().toShortString(),
      ex
    );
  }

  @Pointcut("execution(* *(..)) && @annotation(org.springframework.web.bind.annotation.ExceptionHandler))")
  public void globalExceptionHandlerExecuted() {}

  @Before(value = "globalExceptionHandlerExecuted() && args(ex)", argNames = "ex")
  public void logGlobalExceptionHandler(final Exception ex) throws Throwable {
    LOG.info("Exception thrown", ex);
  }

}
