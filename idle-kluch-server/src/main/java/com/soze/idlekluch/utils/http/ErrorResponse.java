package com.soze.idlekluch.utils.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ErrorResponse {

  private int statusCode;
  private String error;
  private Map<String, Object> data;

  public ErrorResponse(int statusCode, String error) {
    this(statusCode, error, new HashMap<>());
  }

  @JsonCreator
  public ErrorResponse(@JsonProperty("statusCode") int statusCode,
                       @JsonProperty("error") String error,
                       @JsonProperty("data") Map<String, Object> data) {
    this.statusCode = statusCode;
    this.error = Objects.requireNonNull(error);
    this.data = Objects.requireNonNull(data);
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public Map<String, Object> getData() {
    return new HashMap<>(data);
  }

  public void put(final String key, final Object value) {
    Objects.requireNonNull(key);
    data.put(key, value);
  }

  public void addData(String key, Object value) {
    this.data.put(key, value);
  }
}
