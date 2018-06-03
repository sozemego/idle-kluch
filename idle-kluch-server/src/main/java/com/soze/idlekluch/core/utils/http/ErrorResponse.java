package com.soze.idlekluch.core.utils.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ErrorResponse {

  private final int statusCode;
  private final String error;
  private final Map<String, Object> data;

  public ErrorResponse(int statusCode, String error) {
    this(statusCode, error, new HashMap<>());
  }

  @JsonCreator
  public ErrorResponse(@JsonProperty("statusCode") final int statusCode,
                       @JsonProperty("error") final String error,
                       @JsonProperty("data") final Map<String, Object> data) {
    this.statusCode = statusCode;
    this.error = Objects.requireNonNull(error);
    this.data = Objects.requireNonNull(data);
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getError() {
    return error;
  }

  public Map<String, Object> getData() {
    return new HashMap<>(data);
  }

  public void put(final String key, final Object value) {
    Objects.requireNonNull(key);
    data.put(key, value);
  }

  public void addData(final String key, final Object value) {
    Objects.requireNonNull(key);
    Objects.requireNonNull(value);
    this.data.put(key, value);
  }
}
