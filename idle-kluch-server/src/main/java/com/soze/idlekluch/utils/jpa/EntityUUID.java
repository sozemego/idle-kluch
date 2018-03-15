package com.soze.idlekluch.utils.jpa;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class EntityUUID implements Serializable {

  @Column(name = "id")
  @org.hibernate.annotations.Type(type="org.hibernate.type.PostgresUUIDType")
  private UUID id;

  public EntityUUID() {}

  public EntityUUID(UUID id) {
    this.id = Objects.requireNonNull(id);
  }

  public EntityUUID(String id) {
    this(UUID.fromString(id));
  }

  public static EntityUUID randomId() {
    return new EntityUUID(UUID.randomUUID());
  }

  @JsonCreator
  public static EntityUUID fromString(String id) {
    return new EntityUUID(id);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    EntityUUID that = (EntityUUID) o;

    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return id.toString();
  }

}
