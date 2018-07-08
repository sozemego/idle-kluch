package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.kingdom.entity.Resource;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ResourceStorageComponent extends BaseComponent {

  private int capacity;

  @Type(type = "jsonb")
  private List<Resource> resources = new ArrayList<>();

  public ResourceStorageComponent() {
    super(ComponentType.RESOURCE_STORAGE);
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

  @Override
  public BaseComponent copy() {
    return new ResourceStorageComponent(getCapacity());
  }
}
