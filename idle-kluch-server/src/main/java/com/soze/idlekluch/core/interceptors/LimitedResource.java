package com.soze.idlekluch.core.interceptors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public class LimitedResource {

  /**
   * Method on the @Controller that is limited
   */
  private final Object resource;
  /**
   * HTTP Verb (GET, POST etc.)
   */
  private final String method;
  /**
   * URL of the request (/api/0.1/something)
   */
  private final String path;

  public LimitedResource(final Object resource, final String method, final String path) {
    this.resource = Objects.requireNonNull(resource);
    this.method = Objects.requireNonNull(method);
    this.path = Objects.requireNonNull(path);
  }

  @JsonIgnore
  public Object getResource() {
    return resource;
  }

  public String getMethod() {
    return method;
  }

  public String getPath() {
    return path;
  }

  @Override
  public String toString() {
    return "LimitedResource{" +
      "resource=" + resource +
      ", method='" + method + '\'' +
      ", path='" + path + '\'' +
      '}';
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final LimitedResource that = (LimitedResource) o;
    return Objects.equals(getResource(), that.getResource());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getResource());
  }
}
