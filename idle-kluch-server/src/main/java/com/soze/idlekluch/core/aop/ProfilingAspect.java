package com.soze.idlekluch.core.aop;

import com.google.common.base.Stopwatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ProfilingAspect {

  private static final Logger LOG = LoggerFactory.getLogger(ProfilingAspect.class);
  private static final Marker PROFILING_MARKER = MarkerFactory.getMarker("PROFILING");
  private static final String MILlI_SECONDS = "MS";
  private static final String NANO_SECONDS = "NS";

  @Pointcut("execution(* *(..)) && @annotation(com.soze.idlekluch.core.aop.annotations.Profiled))")
  public void profiledMethodExecution() {}

  @Around("profiledMethodExecution()")
  public Object profileMethodCall(final ProceedingJoinPoint pjp) throws Throwable {
    final Stopwatch stopwatch = Stopwatch.createStarted();

    final Object returnValue = pjp.proceed();

    stopwatch.stop();

    LOG.info(
      PROFILING_MARKER,
      "[{}] [{} s] [{}]",
      pjp.getSignature().toShortString(),
      stopwatch.elapsed(),
      pjp.getArgs()
    );

    return returnValue;
  }

}
