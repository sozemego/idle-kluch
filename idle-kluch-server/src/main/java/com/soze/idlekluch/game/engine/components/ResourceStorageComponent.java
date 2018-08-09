package com.soze.idlekluch.game.engine.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.soze.idlekluch.game.engine.components.resourcetransport.ResourceRoute;
import com.soze.idlekluch.kingdom.entity.Resource;
import org.hibernate.annotations.Type;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ResourceStorageComponent extends BaseComponent {

  private int capacity;

  @Type(type = "jsonb")
  private List<Resource> resources = new ArrayList<>();

  private final List<ResourceRoute> routes = new ArrayList<>();

  private int maxRoutes = 20;

  private float transportSpeed;

  private int transportSpeedLevel = 1;

  private float secondsPerRoute;

  private float nextRouteProgress;

  public ResourceStorageComponent() {
    super(ComponentType.RESOURCE_STORAGE);
  }

  public ResourceStorageComponent(final int capacity,
                                  final List<Resource> resources,
                                  final float transportSpeed,
                                  final float secondsPerRoute
  ) {
    this();
    this.capacity = capacity;
    this.resources = Objects.requireNonNull(resources);
    this.transportSpeed = transportSpeed;
    this.secondsPerRoute = secondsPerRoute;
  }

  public ResourceStorageComponent(final int capacity, final float transportSpeed) {
    this();
    this.capacity = capacity;
    this.transportSpeed = transportSpeed;
  }

  public int getCapacity() {
    return capacity;
  }

  public void setCapacity(final int capacity) {
    this.capacity = capacity;
  }

  public void addResource(final Resource resource) {
    Objects.requireNonNull(resource);
    final List<Resource> nextResources = new ArrayList<>(resources);
    nextResources.add(resource);
    resources = nextResources;
  }

  public void removeResource(final Resource resource) {
    final Iterator<Resource> it = resources.iterator();
    while(it.hasNext()) {
      final Resource toRemove = it.next();
      if(toRemove.equals(resource)) {
        it.remove();
        return;
      }
    }
  }

  public List<Resource> getResources() {
    return new ArrayList<>(resources);
  }

  @JsonIgnore
  public int getRemainingCapacity() {
    return getCapacity() - getResources().size();
  }

  @JsonIgnore
  public boolean hasRemainingCapacity() {
    return getResources().size() < getCapacity();
  }

  @Override
  public BaseComponent copy() {
    return new ResourceStorageComponent(getCapacity(), getResources(), getTransportSpeed(), getSecondsPerRoute());
  }

  public void addRoute(final ResourceRoute resourceRoute) {
    this.routes.add(resourceRoute);
  }

  @Transient
  public List<ResourceRoute> getRoutes() {
    return this.routes;
  }

  public void removeRoute(final ResourceRoute resourceRoute) {
    this.routes.remove(resourceRoute);
  }

  @Transient
  public int getMaxRoutes() {
    return maxRoutes;
  }

  public void setMaxRoutes(final int maxRoutes) {
    this.maxRoutes = maxRoutes;
  }

  public float getTransportSpeed() {
    return transportSpeed;
  }

  public void setTransportSpeed(final float transportSpeed) {
    this.transportSpeed = transportSpeed;
  }

  public int getTransportSpeedLevel() {
    return transportSpeedLevel;
  }

  public void setTransportSpeedLevel(final int transportSpeedLevel) {
    this.transportSpeedLevel = transportSpeedLevel;
  }

  public float getSecondsPerRoute() {
    return secondsPerRoute;
  }

  public void setSecondsPerRoute(final float secondsPerRoute) {
    this.secondsPerRoute = secondsPerRoute;
  }

  public float getNextRouteProgress() {
    return nextRouteProgress;
  }

  public void setNextRouteProgress(final float nextRouteProgress) {
    this.nextRouteProgress = nextRouteProgress;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final ResourceStorageComponent that = (ResourceStorageComponent) o;
    return capacity == that.capacity &&
             Objects.equals(resources, that.resources);
  }

  @Override
  public int hashCode() {
    return Objects.hash(capacity, resources);
  }
}
