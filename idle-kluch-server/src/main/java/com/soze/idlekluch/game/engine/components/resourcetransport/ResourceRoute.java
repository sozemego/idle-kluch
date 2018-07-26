package com.soze.idlekluch.game.engine.components.resourcetransport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.kingdom.entity.Resource;

import java.util.Objects;

public class ResourceRoute {

  private final EntityUUID routeId;
  private final Resource resource;
  private final EntityUUID from;
  private final EntityUUID to;
  private float progress = 0f;

  public ResourceRoute(final Resource resource, final EntityUUID from, final EntityUUID to) {
    this.routeId = EntityUUID.randomId();
    this.resource = Objects.requireNonNull(resource);
    this.from = Objects.requireNonNull(from);
    this.to = Objects.requireNonNull(to);
  }

  public EntityUUID getRouteId() {
    return routeId;
  }

  public Resource getResource() {
    return resource;
  }

  public EntityUUID getFrom() {
    return from;
  }

  public EntityUUID getTo() {
    return to;
  }

  @JsonProperty("routeId")
  public String getRouteIdString() {
    return routeId.toString();
  }

  @JsonProperty("from")
  public String getFromString() {
    return from.toString();
  }

  @JsonProperty("to")
  public String getToString() {
    return to.toString();
  }

  public float getProgress() {
    return progress;
  }

  public void setProgress(final float progress) {
    this.progress = progress;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final ResourceRoute that = (ResourceRoute) o;
    return Objects.equals(routeId, that.routeId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(routeId);
  }
}
