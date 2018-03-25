package com.soze.idlekluch.kingdom.entity;

import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "resources")
public class Resource {

  @EmbeddedId
  @AttributeOverride(name = "id", column = @Column(name = "resource_id"))
  private EntityUUID resourceId;

  @Column(name = "name")
  private String name;

  public Resource() {

  }

  public Resource(final EntityUUID resourceId, final String name) {
    this.resourceId = Objects.requireNonNull(resourceId);
    this.name = Objects.requireNonNull(name);
  }

  public EntityUUID getResourceId() {
    return resourceId;
  }

  public void setResourceId(final EntityUUID resourceId) {
    this.resourceId = resourceId;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Resource{" +
      "resourceId=" + resourceId +
      ", name='" + name + '\'' +
      '}';
  }
}
