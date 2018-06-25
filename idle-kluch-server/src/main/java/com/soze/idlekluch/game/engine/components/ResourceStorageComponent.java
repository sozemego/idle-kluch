package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.kingdom.entity.Resource;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "resource_storage_components")
public class ResourceStorageComponent extends BaseComponent {

  @Column(name = "capacity")
  private int capacity;

  @ElementCollection
  @JoinTable(name = "resource_storage", joinColumns = @JoinColumn(name = "entity_id"))
  private List<Resource> resources = new ArrayList<>();

  public ResourceStorageComponent() {
    super(ComponentType.RESOURCE_STORAGE);
  }

  public ResourceStorageComponent(final EntityUUID entityId, final int capacity) {
    this();
    setEntityId(entityId);
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
    resources.add(resource);
  }

  public void removeResource(final Resource resource) {
    Iterator<Resource> it = resources.iterator();
    while(it.hasNext()) {
      Resource toRemove = it.next();
      if(toRemove.equals(resource)) {
        it.remove();
        return;
      }
    }
  }

  @Override
  public BaseComponent copy() {
    return null;
  }
}
