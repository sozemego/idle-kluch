package com.soze.idlekluch.kingdom.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;

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

  @JsonGetter("resourceId")
  public String getResourceIdString() {
    return resourceId.toString();
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
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final Resource resource = (Resource) o;
    return Objects.equals(resourceId, resource.resourceId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resourceId);
  }

  @Override
  public String toString() {
    return "Resource{" +
      "resourceId=" + resourceId +
      ", name='" + name + '\'' +
      '}';
  }
}
