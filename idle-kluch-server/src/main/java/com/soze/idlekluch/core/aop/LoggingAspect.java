package com.soze.idlekluch.core.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Profile("!integration-test")
public class LoggingAspect {

  private static final Logger LOG = LoggerFactory.getLogger(LoggingAspect.class);
  private static final Marker AUTHORIZED_MARKER = MarkerFactory.getMarker("AUTHORIZED");
  private static final Marker EVENT_LISTENER_MARKER = MarkerFactory.getMarker("EVENT_LISTENER");

  @Pointcut("execution(* *(..)) && @annotation(com.soze.idlekluch.core.aop.annotations.AuthLog))")
  public void authorizedMethodExecution() {}

  @Before("authorizedMethodExecution()")
  public void logAuthorizedMethod(final JoinPoint pjp) throws Throwable {
    final String username = getCurrentUsername();
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
    final String username = getCurrentUsername();
    LOG.info(
      AUTHORIZED_MARKER,
      "AUTH AFTER: [{}] [{}]",
      username,
      pjp.getSignature().toShortString()
    );
  }

  @AfterThrowing(pointcut = "authorizedMethodExecution()", throwing = "ex")
  public void afterAuthorizedMethodThrowing(final JoinPoint pjp, final Throwable ex) throws Throwable {
    final String username = getCurrentUsername();
    LOG.info(
      AUTHORIZED_MARKER,
      "AUTH THROWN: [{}] [{}] [{}]",
      username,
      pjp.getSignature().toShortString(),
      ex
    );
  }

  private String getCurrentUsername() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null ? authentication.getName() : "Anonymous";
  }

  @Pointcut("execution(* *(..)) && @annotation(org.springframework.web.bind.annotation.ExceptionHandler))")
  public void globalExceptionHandlerExecuted() {}

  @Before(value = "globalExceptionHandlerExecuted() && args(ex)", argNames = "ex")
  public void logGlobalExceptionHandler(final Exception ex) throws Throwable {
    LOG.info("Exception thrown and handled by GlobalExceptionHandler", ex);
  }

  @Pointcut("execution(* *(..)) && @annotation(org.springframework.messaging.handler.annotation.MessageExceptionHandler)")
  public void messageExceptionHandlerExecuted() {}

  @Before(value = "messageExceptionHandlerExecuted() && args(ex)", argNames = "ex")
  public void logMessageExceptionHandler(final Exception ex) throws Throwable {
    LOG.info("Exception thrown and handled by MessageExceptionHandler", ex);
  }

  @Pointcut("execution(* * (..)) && @annotation(org.springframework.context.event.EventListener)")
  public void eventListenerExecution() {}

  @Before("eventListenerExecution()")
  public void beforeEventListenerExecution(final JoinPoint joinPoint) {
    LOG.info(
      EVENT_LISTENER_MARKER,
      "Method [{}] is handling event [{}]",
      joinPoint.getSignature().toShortString(),
      joinPoint.getArgs()[0]
    );
  }

}
