package com.soze.idlekluch.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class ProfilingAspect {

  private static final Logger LOG = LoggerFactory.getLogger(ProfilingAspect.class);

  private static final Marker PROFILING_MARKER = MarkerFactory.getMarker("PROFILING");


  @Pointcut("execution(* *(..)) && @annotation(com.soze.idlekluch.aop.annotations.Profiled)")
  public void profiledMethodExecution() {}

  @Around("profiledMethodExecution()")
  public Object profileMethodCall(final ProceedingJoinPoint pjp) throws Throwable {
    final long startTime = System.nanoTime();

    final Object returnValue = pjp.proceed();

    final double totalTime = (System.nanoTime() - startTime) / 1e9;

    LOG.info(
      PROFILING_MARKER,
      "Method call [{}] took [{}] ms. Args [{}]",
      pjp.getSignature().getName(),
      String.format("%.6f", totalTime),
      pjp.getArgs()
    );

    return returnValue;
  }

}
