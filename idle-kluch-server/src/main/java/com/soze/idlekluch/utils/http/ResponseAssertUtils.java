package com.soze.idlekluch.utils.http;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;

public class ResponseAssertUtils {

  public static void assertResponseIsOk(Response response) {
    assertEquals(Status.OK, response.getStatusInfo());
  }

  public static void assertResponseIsBadRequest(Response response) {
    assertEquals(Status.BAD_REQUEST, response.getStatusInfo());
  }

  public static void assertResponseIsNotFound(Response response) {
    assertEquals(Status.NOT_FOUND, response.getStatusInfo());
  }

  public static void assertResponseIsUnauthorized(Response response) {
    assertEquals(Status.UNAUTHORIZED, response.getStatusInfo());
  }

  public static void assertResponseIsCreated(Response response) {
    assertEquals(Status.CREATED, response.getStatusInfo());
  }

}
