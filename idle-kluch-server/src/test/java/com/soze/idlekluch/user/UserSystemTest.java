package com.soze.idlekluch.user;

import com.soze.idlekluch.routes.Routes;
import com.soze.idlekluch.user.dto.Jwt;
import com.soze.idlekluch.user.dto.LoginForm;
import com.soze.idlekluch.user.dto.RegisterUserForm;
import com.soze.idlekluch.user.dto.SimpleUserDto;
import com.soze.idlekluch.utils.JsonUtils;
import com.soze.idlekluch.utils.http.ErrorResponse;
import com.soze.idlekluch.utils.http.HttpClient;
import com.soze.idlekluch.utils.sql.DatabaseReset;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.stream.IntStream;

import static com.soze.idlekluch.utils.http.ResponseAssertUtils.*;
import static org.junit.Assert.*;

public class UserSystemTest {

  private final String singleUserPath = Routes.USER_GET_SINGLE;
  private final String createUserPath = Routes.USER_REGISTER;
  private final String deleteUserPath = Routes.USER_DELETE_SINGLE;
  private final String usernameAvailable = Routes.USER_CHECK_AVAILABLE_USERNAME;
  private final String login = Routes.AUTH_LOGIN;

  private HttpClient client;

  @Before
  public void setup() {
    DatabaseReset.resetDatabase();
    client = new HttpClient("http://localhost:8180");
  }

  @Test
  public void testCreateUser() throws Exception {
    String username = "sozemego";
    RegisterUserForm form = new RegisterUserForm(username, "password".toCharArray());

    ResponseEntity response = client.post(form, createUserPath);
    assertResponseIsOk(response);

    SimpleUserDto userDto = getSimpleUserDto(client.get(singleUserPath + "/" + username));
    assertTrue(userDto != null);
    assertEquals(userDto.getUsername(), username);
  }

  @Test
  public void testCreateUserAlreadyExists() throws Exception {
    String username = "sozemego1";
    assertResponseIsOk(client.post(new RegisterUserForm(username, "password".toCharArray()), createUserPath));
    try {
      client.post(new RegisterUserForm(username, "password".toCharArray()), createUserPath);
    } catch (HttpClientErrorException e) {
      assertResponseIsBadRequest(e);
      ErrorResponse errorResponse = getErrorResponse(e);
      assertEquals(errorResponse.getStatusCode(), 400);
      assertEquals(errorResponse.getData().get("field"), "username");
    }
  }

  @Test
  public void testGetUserByUsernameDoesNotExist() throws Exception {
    assertResponseIsNotFound(() -> client.get(singleUserPath + "crazy_user"));
  }

  @Test
  public void testCreateUserWithWhiteSpaceInside() throws Exception {
    try {
      client.post(new RegisterUserForm("some whitespace", "".toCharArray()), createUserPath);
    } catch (HttpClientErrorException e) {
      ErrorResponse errorResponse = getErrorResponse(e);
      assertEquals(errorResponse.getStatusCode(), 400);
      assertEquals(errorResponse.getData().get("field"), "username");
    }

  }

  @Test
  public void testCreateUserWithWhiteSpaceAtTheEnd() throws Exception {
    try {
      client.post(new RegisterUserForm("some_whitespace_after_this   ", "g".toCharArray()), createUserPath);
    } catch (HttpClientErrorException e) {
      ErrorResponse errorResponse = getErrorResponse(e);
      assertEquals(errorResponse.getStatusCode(), 400);
      assertEquals(errorResponse.getData().get("field"), "username");
    }
  }

  @Test
  public void testCreateUserWithWhiteSpaceAtTheBeginning() throws Exception {
    try {
      client.post(new RegisterUserForm("    legit_username", "".toCharArray()), createUserPath);
    } catch (HttpClientErrorException e) {
      ErrorResponse errorResponse = getErrorResponse(e);
      assertEquals(errorResponse.getStatusCode(), 400);
      assertEquals(errorResponse.getData().get("field"), "username");
    }

  }

  @Test
  public void testCreateUserWithWhiteSpaceOnly() throws Exception {

    try {
      client.post(new RegisterUserForm("      ", "".toCharArray()), createUserPath);
    } catch (HttpClientErrorException e) {
      assertResponseIsBadRequest(e);
      ErrorResponse errorResponse = getErrorResponse(e);
      assertEquals(errorResponse.getStatusCode(), 400);
      assertEquals(errorResponse.getData().get("field"), "username");
    }

  }

  @Test
  public void testCreateUserWithIllegalCharacters() throws Exception {
    try {
      client.post(new RegisterUserForm("[]@#$", "".toCharArray()), createUserPath);
    } catch (HttpClientErrorException e) {
      ErrorResponse errorResponse = getErrorResponse(e);
      assertEquals(errorResponse.getStatusCode(), 400);
      assertEquals(errorResponse.getData().get("field"), "username");
    }
  }

  @Test
  public void testCreateUserWithAllAllowableCharacters() throws Exception {
    String username = "qwertyuiopasdfghjklzxcvbnm1234567890-_";
    ResponseEntity response = client.post(new RegisterUserForm(username, "password".toCharArray()), createUserPath);
    assertResponseIsOk(response);
  }

  @Test
  public void testCreateUserNameCaseDoesNotMatter() throws Exception {
    ResponseEntity response = client.post(new RegisterUserForm("case", "pass".toCharArray()), createUserPath);
    assertResponseIsOk(response);

    SimpleUserDto userDto = getSimpleUserDto(client.get(singleUserPath + "/" + "CASE"));
    assertEquals("case", userDto.getUsername());
  }

  @Test
  public void testCreateUserNameRegisterAgainCaseDoesNotMatter() throws Exception {
    ResponseEntity response = client.post(new RegisterUserForm("some_case", "pass".toCharArray()), createUserPath);
    assertResponseIsOk(response);

    try {
      client.post(new RegisterUserForm("SOME_CASE", "pass".toCharArray()), createUserPath);
    } catch (HttpClientErrorException e) {
      ErrorResponse errorResponse = getErrorResponse(e);
      assertEquals(400, errorResponse.getStatusCode());
      assertEquals(errorResponse.getData().get("field"), "username");
    }
  }

  @Test
  public void testCreateUsernameTooLong() throws Exception {
    String longUsername = IntStream.range(0, 26).mapToObj(a -> "" + a).reduce("", (a, b) -> a + b);
    try {
      client.post(new RegisterUserForm(longUsername, "pass".toCharArray()), createUserPath);
    } catch (HttpClientErrorException e) {
      ErrorResponse errorResponse = getErrorResponse(e);
      assertEquals(400, errorResponse.getStatusCode());
      assertEquals(errorResponse.getData().get("field"), "username");
    }
  }

  @Test
  public void testDeleteUserUnauthorized() throws Exception {
    ResponseEntity response = client.post(new RegisterUserForm("some_username", "pass".toCharArray()), createUserPath);
    assertResponseIsOk(response);

    response = client.post(new LoginForm("some_username", "pass".toCharArray()), login);
    Jwt jwt = getJwt(response);
    assertFalse(jwt == null);
    assertFalse(jwt.getJwt().isEmpty());

    try {
      client.deleteWithAuthorizationHeader(deleteUserPath, jwt.getJwt());
    } catch (HttpClientErrorException e) {
      assertResponseIsUnauthorized(e);
    }
  }

  @Test
  public void testDeleteUserAuthorized() throws Exception {
    ResponseEntity response = client.post(new RegisterUserForm("another_username", "pass".toCharArray()), createUserPath);
    assertResponseIsOk(response);

    response = client.post(new LoginForm("another_username", "pass".toCharArray()), login);
    Jwt jwt = getJwt(response);
    assertFalse(jwt == null);
    assertFalse(jwt.getJwt().isEmpty());

    response = client.deleteWithAuthorizationHeader(deleteUserPath, jwt.getJwt());
    assertResponseIsOk(response);

    assertResponseIsNotFound(() -> client.get(singleUserPath + "another_username"));
  }

  @Test
  public void testDeleteUserAuthorizedPreviouslyExisted() throws Exception {
    ResponseEntity response = client.post(new RegisterUserForm("another_username2", "pass".toCharArray()), createUserPath);
    assertResponseIsOk(response);

    response = client.post(new LoginForm("another_username2", "pass".toCharArray()), login);

    Jwt jwt = getJwt(response);
    assertFalse(jwt == null);
    assertFalse(jwt.getJwt().isEmpty());

    response = client.deleteWithAuthorizationHeader(deleteUserPath, jwt.getJwt());
    assertResponseIsOk(response);

    assertResponseIsNotFound(() -> client.get(singleUserPath + "another_username2"));

    try {
      client.post(new RegisterUserForm("another_username2", "pass".toCharArray()), createUserPath);
    } catch (HttpClientErrorException e) {
      ErrorResponse errorResponse = getErrorResponse(e);
      assertEquals(400, errorResponse.getStatusCode());
      assertEquals(errorResponse.getData().get("field"), "username");
    }
  }

  @Test
  public void testDeleteUserDoesNotExist() throws Exception {
    String username = "another_username3";
    ResponseEntity response = client.post(new RegisterUserForm(username, "pass".toCharArray()), createUserPath);
    assertResponseIsOk(response);

    response = client.post(new LoginForm(username, "pass".toCharArray()), login);

    Jwt jwt = getJwt(response);
    assertFalse(jwt == null);
    assertFalse(jwt.getJwt().isEmpty());

    response = client.deleteWithAuthorizationHeader(deleteUserPath, jwt.getJwt());
    assertResponseIsOk(response);

    assertResponseIsUnauthorized(() -> client.deleteWithAuthorizationHeader(deleteUserPath, jwt.getJwt()));
  }

  @Test
  public void testUsernameAvailable() throws Exception {
    ResponseEntity response = client.get(usernameAvailable + "/" + "NOPE");
    boolean available = Boolean.valueOf((String) response.getBody());
    assertEquals(true, available);
  }

  @Test
  public void testUsernameAvailableAlreadyTaken() throws Exception {
    String username = "USERUSER";
    ResponseEntity response = client.post(new RegisterUserForm(username, "pass".toCharArray()), createUserPath);
    assertResponseIsOk(response);

    response = client.get(usernameAvailable + "/" + username);
    boolean available = Boolean.valueOf((String) response.getBody());
    assertEquals(false, available);
  }

  @Test
  public void testUsernameAvailableInvalidUsername() throws Exception {
    String username = "USER_USER2";
    ResponseEntity response = client.post(new RegisterUserForm(username, "pass".toCharArray()), createUserPath);
    assertResponseIsOk(response);

    response = client.get(usernameAvailable + "/" + username);
    boolean available = Boolean.valueOf((String) response.getBody());
    assertEquals(false, available);
  }

  @Test
  public void testUsernameAvailableDeletedUsername() throws Exception {
    String username = "tobedeleted";
    ResponseEntity response = client.post(new RegisterUserForm(username, "pass".toCharArray()), createUserPath);
    assertResponseIsOk(response);

    response = client.post(new LoginForm(username, "pass".toCharArray()), login);

    Jwt jwt = getJwt(response);
    assertFalse(jwt == null);
    assertFalse(jwt.getJwt().isEmpty());

    response = client.deleteWithAuthorizationHeader(deleteUserPath, jwt.getJwt());
    assertResponseIsOk(response);

    response = client.get(usernameAvailable + "/" + username);
    boolean available = Boolean.valueOf((String) response.getBody());
    assertEquals(false, available);
  }

  @Test
  public void testCreateUserEmptyPassword() throws Exception {
    String username = "thisdoesnotmatter";
    try {
      client.post(new RegisterUserForm(username, "".toCharArray()), createUserPath);
    } catch (HttpClientErrorException e) {
      ErrorResponse errorResponse = getErrorResponse(e);
      assertEquals(400, errorResponse.getStatusCode());
      assertEquals(errorResponse.getData().get("field"), "password");
    }
  }

  @Test
  public void testCreateUserPasswordTooLong() throws Exception {
    String username = "some_user_name_again";
    String longPassword = IntStream.range(0, 250).mapToObj(i -> "" + i).reduce("", (a, b) -> a + b);

    try {
      client.post(new RegisterUserForm(username, longPassword.toCharArray()), createUserPath);
    } catch (HttpClientErrorException e) {
      ErrorResponse errorResponse = getErrorResponse(e);
      assertEquals(400, errorResponse.getStatusCode());
      assertEquals(errorResponse.getData().get("field"), "password");
    }
  }

  private Jwt getJwt(ResponseEntity response) {
    return JsonUtils.jsonToObject((String) response.getBody(), Jwt.class);
  }

  private SimpleUserDto getSimpleUserDto(ResponseEntity response) {
    return JsonUtils.jsonToObject((String) response.getBody(), SimpleUserDto.class);
  }

  private ErrorResponse getErrorResponse(ResponseEntity response) {
    return JsonUtils.jsonToObject((String) response.getBody(), ErrorResponse.class);
  }

  private ErrorResponse getErrorResponse(final HttpClientErrorException errorException) {
    return JsonUtils.jsonToObject(errorException.getResponseBodyAsString(), ErrorResponse.class);
  }


}