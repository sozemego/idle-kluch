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

  @Transient
  private final List<ResourceRoute> routes = new ArrayList<>();

  @Transient
  private int maxRoutes = 20;

  public ResourceStorageComponent() {
    super(ComponentType.RESOURCE_STORAGE);
  }

  public ResourceStorageComponent(final int capacity, final List<Resource> resources) {
    this();
    this.capacity = capacity;
    this.resources = Objects.requireNonNull(resources);
  }

  public ResourceStorageComponent(final int capacity) {
    this();
    this.capacity = capacity;
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
    return new ResourceStorageComponent(getCapacity(), getResources());
  }

  public void addRoute(final ResourceRoute resourceRoute) {
    this.routes.add(resourceRoute);
  }

  public List<ResourceRoute> getRoutes() {
    return this.routes;
  }

  public void removeRoute(final ResourceRoute resourceRoute) {
    this.routes.remove(resourceRoute);
  }

  public int getMaxRoutes() {
    return maxRoutes;
  }

  public void setMaxRoutes(final int maxRoutes) {
    this.maxRoutes = maxRoutes;
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
