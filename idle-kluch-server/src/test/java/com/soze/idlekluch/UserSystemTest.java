package com.soze.idlekluch;

import com.soze.idlekluch.user.dto.Jwt;
import com.soze.idlekluch.user.dto.LoginForm;
import com.soze.idlekluch.user.dto.RegisterUserForm;
import com.soze.idlekluch.user.dto.SimpleUserDto;
import com.soze.idlekluch.utils.JsonUtils;
import com.soze.idlekluch.utils.http.ErrorResponse;
import com.soze.idlekluch.utils.http.HttpClient;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class UserSystemTest {

  private final String singleUserPath = "single/";
  private final String createUserPath = "register/";
  private final String deleteUserPath = "single/delete/";
  private final String usernameAvailable = "single/available/";
  private final String login = "auth/login/";

  private HttpClient client;

  @Before
  public void setup() {
    DatabaseReset.resetDatabase();
    client = new HttpClient("http://localhost:8180/api/0.1/user/");
  }

  @Test
  public void testCreateUser() throws Exception {
    String username = "sozemego";
    RegisterUserForm form = new RegisterUserForm(username, "password".toCharArray());

    Response response = client.post(form, createUserPath);
    assertResponseIsOk(response);

    SimpleUserDto userDto = getSimpleUserDto(client.get(singleUserPath + username));
    assertTrue(userDto != null);
    assertEquals(userDto.getUsername(), username);
  }

  @Test
  public void testCreateUserAlreadyExists() throws Exception {
    String username = "sozemego1";
    assertResponseIsOk(client.post(new RegisterUserForm(username, "password".toCharArray()), createUserPath));
    Response response = client.post(new RegisterUserForm(username, "password".toCharArray()), createUserPath);
    assertResponseIsBadRequest(response);

    ErrorResponse errorResponse = getErrorResponse(response);
    assertEquals(errorResponse.getStatusCode(), 400);
    assertEquals(errorResponse.getData().get("field"), "username");
  }

  @Test
  public void testGetUserByUsernameDoesNotExist() throws Exception {
    assertResponseIsNotFound(client.get(singleUserPath + "crazy_user"));
  }

  @Test
  public void testCreateUserWithWhiteSpaceInside() throws Exception {
    Response response = client.post(new RegisterUserForm("some whitespace", "".toCharArray()), createUserPath);

    ErrorResponse errorResponse = getErrorResponse(response);
    assertEquals(errorResponse.getStatusCode(), 400);
    assertEquals(errorResponse.getData().get("field"), "username");
  }

  @Test
  public void testCreateUserWithWhiteSpaceAtTheEnd() throws Exception {
    Response response = client.post(new RegisterUserForm("some_whitespace_after_this   ", "g".toCharArray()), createUserPath);

    ErrorResponse errorResponse = getErrorResponse(response);
    assertEquals(errorResponse.getStatusCode(), 400);
    assertEquals(errorResponse.getData().get("field"), "username");
  }

  @Test
  public void testCreateUserWithWhiteSpaceAtTheBeginning() throws Exception {
    Response response = client.post(new RegisterUserForm("    legit_username", "".toCharArray()), createUserPath);

    ErrorResponse errorResponse = getErrorResponse(response);
    assertEquals(errorResponse.getStatusCode(), 400);
    assertEquals(errorResponse.getData().get("field"), "username");
  }

  @Test
  public void testCreateUserWithWhiteSpaceOnly() throws Exception {
    Response response = client.post(new RegisterUserForm("      ", "".toCharArray()), createUserPath);

    ErrorResponse errorResponse = getErrorResponse(response);
    assertEquals(errorResponse.getStatusCode(), 400);
    assertEquals(errorResponse.getData().get("field"), "username");
  }

  @Test
  public void testCreateUserWithIllegalCharacters() throws Exception {
    Response response = client.post(new RegisterUserForm("[]@#$", "".toCharArray()), createUserPath);

    ErrorResponse errorResponse = getErrorResponse(response);
    assertEquals(errorResponse.getStatusCode(), 400);
    assertEquals(errorResponse.getData().get("field"), "username");
  }

  @Test
  public void testCreateUserWithAllAllowableCharacters() throws Exception {
    String username = "qwertyuiopasdfghjklzxcvbnm1234567890-_";
    Response response = client.post(new RegisterUserForm(username, "password".toCharArray()), createUserPath);
    assertResponseIsOk(response);
  }

  @Test
  public void testCreateUserNameCaseDoesNotMatter() throws Exception {
    Response response = client.post(new RegisterUserForm("case", "pass".toCharArray()), createUserPath);
    assertResponseIsOk(response);

    SimpleUserDto userDto = getSimpleUserDto(client.get(singleUserPath + "CASE"));
    assertEquals("case", userDto.getUsername());
  }

  @Test
  public void testCreateUserNameRegisterAgainCaseDoesNotMatter() throws Exception {
    Response response = client.post(new RegisterUserForm("some_case", "pass".toCharArray()), createUserPath);
    assertResponseIsOk(response);
    response = client.post(new RegisterUserForm("SOME_CASE", "pass".toCharArray()), createUserPath);
    ErrorResponse errorResponse = getErrorResponse(response);
    assertEquals(400, errorResponse.getStatusCode());
    assertEquals(errorResponse.getData().get("field"), "username");
  }

  @Test
  public void testCreateUsernameTooLong() throws Exception {
    String longUsername = IntStream.range(0, 26).mapToObj(a -> "" + a).reduce("", (a, b) -> a + b);
    Response response = client.post(new RegisterUserForm(longUsername, "pass".toCharArray()), createUserPath);
    ErrorResponse errorResponse = getErrorResponse(response);
    assertEquals(400, errorResponse.getStatusCode());
    assertEquals(errorResponse.getData().get("field"), "username");
  }

  @Test
  public void testDeleteUserUnauthorized() throws Exception {
    Response response = client.post(new RegisterUserForm("some_username", "pass".toCharArray()), createUserPath);
    assertResponseIsOk(response);

    assertResponseIsUnauthorized(client.delete(deleteUserPath));
  }

  @Test
  public void testDeleteUserAuthorized() throws Exception {
    Response response = client.post(new RegisterUserForm("another_username", "pass".toCharArray()), createUserPath);
    assertResponseIsOk(response);

    response = client.post(new LoginForm("another_username", "pass".toCharArray()), login);
    Jwt jwt = getJwt(response);
    assertFalse(jwt == null);
    assertFalse(jwt.getJwt().isEmpty());

    response = client.deleteWithAuthorizationHeader(deleteUserPath, jwt.getJwt());
    assertResponseIsOk(response);

    assertResponseIsNotFound(client.get(singleUserPath + "another_username"));
  }

  @Test
  public void testDeleteUserAuthorizedPreviouslyExisted() throws Exception {
    Response response = client.post(new RegisterUserForm("another_username2", "pass".toCharArray()), createUserPath);
    assertResponseIsOk(response);

    response = client.post(new LoginForm("another_username2", "pass".toCharArray()), login);

    Jwt jwt = getJwt(response);
    assertFalse(jwt == null);
    assertFalse(jwt.getJwt().isEmpty());

    response = client.deleteWithAuthorizationHeader(deleteUserPath, jwt.getJwt());
    assertResponseIsOk(response);

    assertResponseIsNotFound(client.get(singleUserPath + "another_username2"));

    response = client.post(new RegisterUserForm("another_username2", "pass".toCharArray()), createUserPath);
    ErrorResponse errorResponse = getErrorResponse(response);
    assertEquals(400, errorResponse.getStatusCode());
    assertEquals(errorResponse.getData().get("field"), "username");
  }

  @Test
  public void testDeleteUserDoesNotExist() throws Exception {
    String username = "another_username3";
    Response response = client.post(new RegisterUserForm(username, "pass".toCharArray()), createUserPath);
    assertResponseIsOk(response);

    response = client.post(new LoginForm(username, "pass".toCharArray()), login);

    Jwt jwt = getJwt(response);
    assertFalse(jwt == null);
    assertFalse(jwt.getJwt().isEmpty());

    response = client.deleteWithAuthorizationHeader(deleteUserPath, jwt.getJwt());
    assertResponseIsOk(response);

    assertResponseIsUnauthorized(client.deleteWithAuthorizationHeader(deleteUserPath, jwt.getJwt()));
  }

  @Test
  public void testUsernameAvailable() throws Exception {
    Response response = client.get(usernameAvailable + "NOPE");
    boolean available = Boolean.valueOf(response.readEntity(String.class));
    assertEquals(true, available);
  }

  @Test
  public void testUsernameAvailableAlreadyTaken() throws Exception {
    String username = "USERUSER";
    Response response = client.post(new RegisterUserForm(username, "pass".toCharArray()), createUserPath);
    assertResponseIsOk(response);

    response = client.get(usernameAvailable + username);
    boolean available = Boolean.valueOf(response.readEntity(String.class));
    assertEquals(false, available);
  }

  @Test
  public void testUsernameAvailableInvalidUsername() throws Exception {
    String username = "USER_USER2";
    Response response = client.post(new RegisterUserForm(username, "pass".toCharArray()), createUserPath);
    assertResponseIsOk(response);

    response = client.get(usernameAvailable + username);
    boolean available = Boolean.valueOf(response.readEntity(String.class));
    assertEquals(false, available);
  }

  @Test
  public void testUsernameAvailableDeletedUsername() throws Exception {
    String username = "tobedeleted";
    Response response = client.post(new RegisterUserForm(username, "pass".toCharArray()), createUserPath);
    assertResponseIsOk(response);

    response = client.post(new LoginForm(username, "pass".toCharArray()), login);

    Jwt jwt = getJwt(response);
    assertFalse(jwt == null);
    assertFalse(jwt.getJwt().isEmpty());

    response = client.deleteWithAuthorizationHeader(deleteUserPath, jwt.getJwt());
    assertResponseIsOk(response);

    response = client.get(usernameAvailable + username);
    boolean available = Boolean.valueOf(response.readEntity(String.class));
    assertEquals(false, available);
  }

  @Test
  public void testCreateUserEmptyPassword() throws Exception {
    String username = "thisdoesnotmatter";
    Response response = client.post(new RegisterUserForm(username, "".toCharArray()), createUserPath);

    ErrorResponse errorResponse = getErrorResponse(response);
    assertEquals(400, errorResponse.getStatusCode());
    assertEquals(errorResponse.getData().get("field"), "password");
  }

  @Test
  public void testCreateUserPasswordTooLong() throws Exception {
    String username = "some_user_name_again";

    String longPassword = IntStream.range(0, 250).mapToObj(i -> "" + i).reduce("", (a, b) -> a + b);

    Response response = client.post(new RegisterUserForm(username, longPassword.toCharArray()), createUserPath);
    ErrorResponse errorResponse = getErrorResponse(response);
    assertEquals(400, errorResponse.getStatusCode());
    assertEquals(errorResponse.getData().get("field"), "password");
  }

  private Jwt getJwt(Response response) {
    return JsonUtils.jsonToObject(response.readEntity(String.class), Jwt.class);
  }

  private SimpleUserDto getSimpleUserDto(Response response) {
    return JsonUtils.jsonToObject(response.readEntity(String.class), SimpleUserDto.class);
  }

  private ErrorResponse getErrorResponse(Response response) {
    return JsonUtils.jsonToObject(response.readEntity(String.class), ErrorResponse.class);
  }


}
