package com.soze.idlekluch.kingdom;

import com.soze.idlekluch.BaseAuthTest;
import com.soze.idlekluch.kingdom.dto.KingdomDto;
import com.soze.idlekluch.kingdom.dto.RegisterKingdomForm;
import com.soze.idlekluch.routes.Routes;
import com.soze.idlekluch.utils.CommonUtils;
import com.soze.idlekluch.utils.JsonUtils;
import com.soze.idlekluch.utils.http.ErrorResponse;
import com.soze.idlekluch.utils.sql.DatabaseReset;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import static com.soze.idlekluch.utils.http.ResponseAssertUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KingdomSystemTest extends BaseAuthTest {

  private static final String REGISTER_KINGDOM = Routes.KINGDOM_CREATE;
  private static final String GET_KINGDOM = Routes.KINGDOM_GET;
  private static final String DELETE_KINGDOM = Routes.KINGDOM_DELETE;
  private static final String KINGDOM_OWN = Routes.KINGDOM_OWN;

  @Before
  public void setup() {
    super.setup();
    DatabaseReset.resetDatabase();
  }

  @Test
  public void testRegisterKingdom() {
    final String username = "Username";
    login(username);
    final String kingdomName = "cool_kingdom";
    final RegisterKingdomForm form = new RegisterKingdomForm(kingdomName);
    final ResponseEntity response = client.post(form, REGISTER_KINGDOM);
    assertResponseIsCreated(response);

    final ResponseEntity kingdomResponse = client.get(GET_KINGDOM + "/" + kingdomName);
    final KingdomDto dto = JsonUtils.jsonToObject((String) kingdomResponse.getBody(), KingdomDto.class);

    assertTrue(dto.getName().equals(kingdomName));
    assertTrue(dto.getOwner().equals(username));
    assertTrue(dto.getCreatedAt() != null);

    final ResponseEntity ownKingdomResponse = client.get(KINGDOM_OWN);
    final KingdomDto ownKingdomDto = JsonUtils.jsonToObject((String) ownKingdomResponse.getBody(), KingdomDto.class);
    assertTrue(ownKingdomDto.getName().equals(kingdomName));
    assertTrue(ownKingdomDto.getOwner().equals(username));
    assertTrue(ownKingdomDto.getCreatedAt() != null);
  }

  @Test(expected = HttpClientErrorException.class)
  public void testRegisterKingdomNotAuthorized() {
    final String kingdomName = "cool_kingdom";
    final RegisterKingdomForm form = new RegisterKingdomForm(kingdomName);

    try {
      client.post(form, REGISTER_KINGDOM);
    } catch (HttpClientErrorException e) {
      assertResponseIsForbidden(e);
      throw e;
    }
  }

  @Test(expected = HttpClientErrorException.class)
  public void testRegisterKingdomIllegalName() {
    final String username = "Username";
    login(username);
    final String kingdomName = "cool_%%^$^_another_one";
    final RegisterKingdomForm form = new RegisterKingdomForm(kingdomName);

    try {
      client.post(form, REGISTER_KINGDOM);
    } catch (HttpClientErrorException e) {
      assertResponseIsBadRequest(e);
      final ErrorResponse errorResponse = getErrorResponse(e);
      assertEquals("name", errorResponse.getData().get("field"));
      throw e;
    }

  }

  @Test(expected = HttpClientErrorException.class)
  public void testRegisterKingdomNameTooLong() {
    final String username = "Username";
    login(username);
    final String kingdomName = CommonUtils.generateRandomString(500);
    final RegisterKingdomForm form = new RegisterKingdomForm(kingdomName);

    try {
      client.post(form, REGISTER_KINGDOM);
    } catch (HttpClientErrorException e) {
      assertResponseIsBadRequest(e);
      final ErrorResponse errorResponse = getErrorResponse(e);
      assertEquals("name", errorResponse.getData().get("field"));
      throw e;
    }
  }

  @Test(expected = HttpClientErrorException.class)
  public void testRegisterKingdomAlreadyExists() {
    final String username = "Username";
    login(username);
    final String kingdomName = "normal";
    final RegisterKingdomForm form = new RegisterKingdomForm(kingdomName);

    client.post(form, REGISTER_KINGDOM);

    final String anotherUsername = "another_user";
    login(anotherUsername);

    try {
      client.post(form, REGISTER_KINGDOM);
    } catch (HttpClientErrorException e) {
      assertResponseIsBadRequest(e);
      throw e;
    }

  }

  @Test(expected = HttpClientErrorException.class)
  public void testUserAlreadyHasKingdom() {
    final String username = "Username";
    login(username);
    final String kingdomName = "normal";
    final RegisterKingdomForm form = new RegisterKingdomForm(kingdomName);
    client.post(form, REGISTER_KINGDOM);

    final RegisterKingdomForm differentForm = new RegisterKingdomForm("different_name");
    try {
      client.post(differentForm, REGISTER_KINGDOM);
    } catch (HttpClientErrorException e) {
      assertResponseIsBadRequest(e);
      throw e;
    }
  }

  @Test(expected = HttpClientErrorException.class)
  public void testDeleteKingdomUnauthorized() {
    try {
      client.delete(DELETE_KINGDOM);
    } catch (HttpClientErrorException e) {
      assertResponseIsForbidden(e);
      throw e;
    }
  }

  @Test(expected = HttpClientErrorException.class)
  public void testDeleteKingdomAuthorized() {
    final String username = "cool_username";
    login(username);
    final String kingdomName = "cool_fine_kingdom";
    final RegisterKingdomForm form = new RegisterKingdomForm(kingdomName);
    final ResponseEntity response = client.post(form, REGISTER_KINGDOM);
    assertResponseIsCreated(response);

    final ResponseEntity kingdomResponse = client.get(GET_KINGDOM + "/" + kingdomName);
    final KingdomDto dto = JsonUtils.jsonToObject((String) kingdomResponse.getBody(), KingdomDto.class);

    assertTrue(dto.getName().equals(kingdomName));
    assertTrue(dto.getOwner().equals(username));
    assertTrue(dto.getCreatedAt() != null);

    client.delete(DELETE_KINGDOM);

    try {
      client.get(GET_KINGDOM);
    } catch (HttpClientErrorException e) {
      assertResponseIsNotFound(e);
      throw e;
    }
  }

  @Test(expected = HttpClientErrorException.class)
  public void testGetOwnKingdomUnauthorized() {
    try {
      client.get(KINGDOM_OWN);
    } catch (HttpClientErrorException e) {
      assertResponseIsForbidden(e);
      throw e;
    }
  }

  private ErrorResponse getErrorResponse(final HttpClientErrorException errorException) {
    return JsonUtils.jsonToObject(errorException.getResponseBodyAsString(), ErrorResponse.class);
  }

}
