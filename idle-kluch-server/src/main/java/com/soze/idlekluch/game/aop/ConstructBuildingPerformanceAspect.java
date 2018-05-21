package com.soze.idlekluch.game.aop;

import com.soze.idlekluch.game.message.BuildBuildingForm;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Measures how long does constructing a building take.
 */
@Aspect
@Component
public class ConstructBuildingPerformanceAspect {

  private static final Logger LOG = LoggerFactory.getLogger(ConstructBuildingPerformanceAspect.class);

  @Pointcut("execution(* *.handleBuildBuildingMessage(..))")
  public void handleBuildBuildingMessage() {}

  @Around("handleBuildBuildingMessage() && args(owner, form)")
  public Object measureConstructBuildingPerformance(final ProceedingJoinPoint pjp,
                                                    final String owner,
                                                    final BuildBuildingForm form) throws Throwable {
    final long startTime = System.nanoTime();

    final Object returnValue = pjp.proceed();

    final long totalTime = System.nanoTime() - startTime;
    LOG.info(
      "Took [{}] ms for user [{}] to construct buildingId [{}]",
      TimeUnit.NANOSECONDS.toMillis(totalTime),
      owner,
      form.getBuildingId()
    );

    return returnValue;
  }


}
