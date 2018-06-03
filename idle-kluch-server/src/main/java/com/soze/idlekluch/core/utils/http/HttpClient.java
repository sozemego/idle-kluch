package com.soze.idlekluch.core.utils.http;


import com.soze.idlekluch.core.utils.JsonUtils;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A simple wrapper around a http client.
 * Used mostly for testing.
 */
public class HttpClient {

  private final String url;
  private String token = null;

  public HttpClient(final String url) {
    this.url = Objects.requireNonNull(url);
  }

  public HttpClient(final URI uri) {
    this.url = Objects.requireNonNull(uri.toString());
  }

  /**
   * Performs a GET request to the base url given to this object.
   */
  public ResponseEntity get() {
    return get(null);
  }

  /**
   * Performs a GET request to the base url with given path appended.
   * By default, it requests a application/json from the endpoint.
   */
  public ResponseEntity get(final String path) throws HttpClientErrorException {
    final String finalPath = url + (path != null ? path : "");
    System.out.println("Making a GET request to " + finalPath);

    final RestTemplate restTemplate = createClient();
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Authorization", "Bearer " + token);
    httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    final HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

    return warnNotFound(() -> restTemplate.exchange(finalPath, HttpMethod.GET, entity, String.class));
  }

  /**
   * Performs a GET request to the base url with given path appended.
   * By default, it requests a application/json from the endpoint.
   */
  public ResponseEntity getPlainText(final String path) throws HttpClientErrorException {
    final String finalPath = url + (path != null ? path : "");
    System.out.println("Making a get request to " + finalPath);

    final RestTemplate restTemplate = createClient();
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Authorization", "Bearer " + token);
    httpHeaders.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
    final HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

    return warnNotFound(() -> restTemplate.exchange(finalPath, HttpMethod.GET, entity, String.class));
  }

  /**
   * Stringifies a given body and performs a POST request to the base url.
   */
  public ResponseEntity post(final Object body) throws HttpClientErrorException {
    return post(body, null);
  }

  /**
   * Stringifies a given body and performs a POST
   * request to the base url with path parameter appended.
   */
  public ResponseEntity post(final Object body, final String path) throws HttpClientErrorException {
    Objects.requireNonNull(body);

    final String finalPath = url + (path != null ? path : "");
    final String json = JsonUtils.objectToJson(body);
    System.out.println("Making a POST request to " + finalPath + " with body " + json);

    final RestTemplate restTemplate = createClient();
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Authorization", "Bearer " + token);
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    final HttpEntity<String> entity = new HttpEntity<>(json, httpHeaders);

    return warnNotFound(() -> restTemplate.exchange(finalPath, HttpMethod.POST, entity, String.class));
  }

  public ResponseEntity deleteWithAuthorizationHeader(final String path, final String bearerToken) throws HttpClientErrorException {
    Objects.requireNonNull(path);
    Objects.requireNonNull(bearerToken);

    final String finalPath = url + (path != null ? path : "");
    System.out.println("Making a DELETE request to " + finalPath + " with bearer " + bearerToken);

    final RestTemplate restTemplate = createClient();
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Authorization", "Bearer " + bearerToken);
    final HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

    return warnNotFound(() -> restTemplate.exchange(finalPath, HttpMethod.DELETE, entity, String.class));
  }

  /**
   * Sends a DELETE request to the base path.
   */
  public ResponseEntity delete() throws HttpClientErrorException {

    final String finalPath = url;
    System.out.println("Making a DELETE request to " + finalPath);

    final RestTemplate restTemplate = createClient();
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Authorization", "Bearer " + token);
    final HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

    return warnNotFound(() -> restTemplate.exchange(finalPath, HttpMethod.DELETE, entity, String.class));
  }

  /**
   * Sends a DELETE request to the base path, with given path appended.
   *
   * @param path
   * @return
   */
  public ResponseEntity delete(final String path) throws HttpClientErrorException {
    final String finalPath = url + (path != null ? path : "");
    System.out.println("Making a DELETE request to " + finalPath);

    final RestTemplate restTemplate = createClient();
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Authorization", "Bearer " + token);
    final HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

    return warnNotFound(() -> restTemplate.exchange(finalPath, HttpMethod.DELETE, entity, String.class));
  }

  public void setToken(final String token) {
    this.token = token;
  }

  private RestTemplate createClient() {
    return new RestTemplate();
  }

  private ResponseEntity warnNotFound(ResponseEntity response) {
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      System.out.println("Warning, response had status code 404");
    }
    return response;
  }

  private ResponseEntity warnNotFound(final Supplier<ResponseEntity> producer) {
    try {
      return producer.get();
    } catch (HttpClientErrorException e) {
      if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
        System.out.println("Warning, response had status code 404");
      }
      throw e;
    }
  }

}
