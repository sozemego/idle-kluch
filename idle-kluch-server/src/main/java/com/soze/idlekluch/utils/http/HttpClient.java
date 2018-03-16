package com.soze.idlekluch.utils.http;


import com.soze.idlekluch.utils.JsonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Objects;

/**
 * A simple wrapper around a http client.
 * Used mostly for testing.
 * TODO extract an interface for the client, so that it can be mocked in places which need it
 */
public class HttpClient {

  private final String url;
  private String token = null;

  public HttpClient(String url) {
    this.url = Objects.requireNonNull(url);
  }

  public HttpClient(URI uri) {
    this.url = Objects.requireNonNull(uri.toString());
  }

  /**
   * Performs a GET request to the base url given to this object.
   */
  public ResponseEntity get() {

    String finalPath = url;
    System.out.println("Making a get request to " + finalPath);

    return warnNotFound(createClient()
      .target(url)
      .request()
      .header("Authorization", "Bearer " + token)
      .get());
  }

  /**
   * Performs a GET request to the base url with given path appended.
   * By default, it requests a application/json from the endpoint.
   */
  public ResponseEntity get(String path) {
    Objects.requireNonNull(path);

    String finalPath = url + path;
    System.out.println("Making a GET request to " + finalPath);

    return warnNotFound(createClient()
      .target(url + path)
      .request()
      .header("Authorization", "Bearer " + token)
      .accept(MediaType.APPLICATION_JSON)
      .get());
  }

  /**
   * Performs a GET request to the base url with given path appended.
   * By default, it requests a application/json from the endpoint.
   */
  public ResponseEntity getPlainText(String path) {
    Objects.requireNonNull(path);

    String finalPath = url + path;
    System.out.println("Making a GET request to " + finalPath);

    return warnNotFound(createClient()
      .target(url + path)
      .request()
      .header("Authorization", "Bearer " + token)
      .accept(MediaType.TEXT_PLAIN)
      .get());
  }

  /**
   * Stringifies a given body and performs a POST request to the base url.
   */
  public ResponseEntity post(Object body) {
    Objects.requireNonNull(body);

    String finalPath = url;
    String json = JsonUtils.objectToJson(body);
    System.out.println("Making a POST request to " + finalPath + " with body " + json);

    return warnNotFound(createClient()
      .target(url)
      .request()
      .header("Authorization", "Bearer " + token)
      .post(Entity.json(json)));
  }

  /**
   * Stringifies a given body and performs a POST
   * request to the base url with path parameter appended.
   */
  public ResponseEntity post(Object body, String path) {
    Objects.requireNonNull(body);
    Objects.requireNonNull(path);

    String finalPath = url + path;
    System.out.println("Making a POST request to " + finalPath);

    return warnNotFound(createClient()
      .target(finalPath)
      .request()
      .header("Authorization", "Bearer " + token)
      .post(Entity.json(JsonUtils.objectToJson(body))));
  }

  public Response deleteWithAuthorizationHeader(String path, String headerValue) {

    String finalPath = url + path;
    System.out.println("Deleting with authorization header. Path: " + finalPath + ", header value: " + headerValue);


    return warnNotFound(createClient()
      .target(finalPath)
      .request()
      .header("Authorization", "Bearer " + headerValue)
      .delete());
  }

  /**
   * Sends a DELETE request to the base path.
   */
  public ResponseEntity delete() {

    System.out.println("Sending a DELETE request to " + url);

    return warnNotFound(createClient()
      .target(url)
      .request()
      .header("Authorization", "Bearer " + token)
      .delete());
  }

  /**
   * Sends a DELETE request to the base path, with given path appended.
   * @param path
   * @return
   */
  public ResponseEntity delete(String path) {
    Objects.requireNonNull(path);

    String finalPath = url + path;
    System.out.println("Sending a DELETE request to " + finalPath);

    return warnNotFound(createClient()
      .target(finalPath)
      .request()
      .header("Authorization", "Bearer " + token)
      .delete());
  }

  public void setToken(String token) {
    this.token = token;
  }

  private Client createClient() {
    return ClientBuilder.newClient();
  }

  private ResponseEntity warnNotFound(ResponseEntity response) {
    if(response.getStatusCode() == HttpStatus.NOT_FOUND) {
      System.out.println("Warning, response had status code 404");
    }
    return response;
  }

}
