package com.soze.idlekluch.utils;

import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PoissonDiscSamplerTest {

  /**
   * Minimum distance between points for the Poisson-Disc Sampling algorithm.
   */
  private static final int MINIMUM_DISTANCE = 10;

  private PoissonDiscSampler createSampler() {
    return createSampler(new ArrayList<>());
  }

  private PoissonDiscSampler createSampler(final List<Point> currentPoints) {
    return new PoissonDiscSampler(currentPoints, MINIMUM_DISTANCE);
  }

  @Test
  public void testGetNextPointEmptyList() {
    final PoissonDiscSampler sampler = createSampler();
    final Point firstPoint = sampler.nextPoint();
    assertEquals(0, firstPoint.x);
    assertEquals(0, firstPoint.y);
  }

  @Test
  public void testGetManyPoints() {
    final PoissonDiscSampler sampler = createSampler();
    final int pointsToGenerate = 200;
    final List<Point> generatedPoints = new ArrayList<>();
    for(int i = 0; i < pointsToGenerate; i++) {
      final Point point = sampler.nextPoint();
      assertTrue(isMinimumDistanceAwayFromAllPoints(point, generatedPoints));
      generatedPoints.add(point);
    }
    System.out.println(generatedPoints);
  }

  private boolean isMinimumDistanceAwayFromAllPoints(final Point nextPoint, final List<Point> points) {
    for(final Point point: points) {
      final double distance = Math.hypot(nextPoint.x - point.x, nextPoint.y - point.y);
      if(distance < MINIMUM_DISTANCE) {
        return false;
      }
    }
    return true;
  }


}