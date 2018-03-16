package com.soze.idlekluch.utils.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;


public class ResponseAssertUtils {

  public static void assertResponseIsOk(final ResponseEntity response) {
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  public static void assertResponseIsBadRequest(final ResponseEntity response) {
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  public static void assertResponseIsNotFound(final ResponseEntity response) {
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  public static void assertResponseIsUnauthorized(final ResponseEntity response) {
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  public static void assertResponseIsCreated(final ResponseEntity response) {
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

}
