package com.soze.idlekluch.core.utils.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class ResponseAssertUtils {

  public static void assertResponseIsOk(final ResponseEntity response) {
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  public static void assertResponseIsBadRequest(final ResponseEntity response) {
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  public static void assertResponseIsBadRequest(final HttpClientErrorException response) {
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  public static void assertResponseIsBadRequest(final Runnable command) {
    assertCommandIs(command, HttpStatus.BAD_REQUEST);
  }

  public static void assertResponseIsNotFound(final ResponseEntity response) {
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  public static void assertResponseIsNotFound(final HttpClientErrorException response) {
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  public static void assertResponseIsNotFound(final Runnable command) {
    assertCommandIs(command, HttpStatus.NOT_FOUND);
  }

  public static void assertResponseIsUnauthorized(final ResponseEntity response) {
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  public static void assertResponseIsUnauthorized(final HttpClientErrorException response) {
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  public static void assertResponseIsForbidden(final HttpClientErrorException response) {
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  public static void assertResponseIsForbidden(final Runnable command) {
    assertCommandIs(command, HttpStatus.FORBIDDEN);
  }

  public static void assertResponseIsUnauthorized(final Runnable command) {
    assertCommandIs(command, HttpStatus.UNAUTHORIZED);
  }

  public static void assertResponseIsCreated(final ResponseEntity response) {
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  public static void assertCommandIs(final Runnable command, final HttpStatus status) {
    try {
      command.run();
      fail("It is supposed to throw");
    } catch (HttpClientErrorException e) {
      assertEquals(status, e.getStatusCode());
    }
  }

}
