package com.soze.idlekluch.kingdom;

import com.soze.idlekluch.BaseAuthTest;
import com.soze.idlekluch.kingdom.dto.KingdomDto;
import com.soze.idlekluch.kingdom.dto.RegisterKingdomForm;
import com.soze.idlekluch.routes.Routes;
import com.soze.idlekluch.utils.CommonUtils;
import com.soze.idlekluch.utils.JsonUtils;
import com.soze.idlekluch.utils.http.ErrorResponse;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import static com.soze.idlekluch.utils.http.ResponseAssertUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KingdomSystemTest extends BaseAuthTest {

  private static final String CHECK_NAME_AVAILABLE = Routes.KINGDOM_BASE + Routes.KINGDOM_CHECK_NAME_AVAILABLE;
  private static final String REGISTER_KINGDOM = Routes.KINGDOM_BASE + Routes.KINGDOM_CREATE;
  private static final String GET_KINGDOM = Routes.KINGDOM_BASE + Routes.KINGDOM_GET;
  private static final String DELETE_KINGDOM = Routes.KINGDOM_BASE + Routes.KINGDOM_DELETE;

  @Test
  public void testIsNameAvailable() throws Exception {
    final ResponseEntity response = client.get(CHECK_NAME_AVAILABLE + "/superbingdom");
    boolean available = Boolean.valueOf((String) response.getBody());
    assertEquals(true, available);
  }

  @Test
  public void testIsNameAvailableNotAvailable() {
    login("Username");
    final String kingdomName = "cool kingdom";
    final RegisterKingdomForm form = new RegisterKingdomForm(kingdomName);
    client.post(form, REGISTER_KINGDOM);

    final ResponseEntity response = client.get(CHECK_NAME_AVAILABLE + "/" + kingdomName);
    boolean available = Boolean.valueOf((String) response.getBody());
    assertEquals(false, available);
  }

  @Test
  public void testIsNameAvailableTooLong() {
    final String kingdomName = CommonUtils.generateRandomString(500);
    final ResponseEntity response = client.get(CHECK_NAME_AVAILABLE + "/" + kingdomName);
    boolean available = Boolean.valueOf((String) response.getBody());
    assertEquals(false, available);
  }

  @Test
  public void testRegisterKingdom() {
    final String username = "Username";
    login(username);
    final String kingdomName = "cool kingdom";
    final RegisterKingdomForm form = new RegisterKingdomForm(kingdomName);
    final ResponseEntity response = client.post(form, REGISTER_KINGDOM);
    assertResponseIsCreated(response);

    final ResponseEntity kingdomResponse = client.get(GET_KINGDOM);
    final KingdomDto dto = JsonUtils.jsonToObject((String) kingdomResponse.getBody(), KingdomDto.class);

    assertTrue(dto.getName() == kingdomName);
    assertTrue(dto.getOwner() == username);
    assertTrue(dto.getCreatedAt() != null);
  }

  @Test
  public void testRegisterKingdomNotAuthorized() {
    final String kingdomName = "cool kingdom";
    final RegisterKingdomForm form = new RegisterKingdomForm(kingdomName);

    try {
      client.post(form, REGISTER_KINGDOM);
    } catch (HttpClientErrorException e) {
      assertResponseIsForbidden(e);
    }
  }

  @Test
  public void testRegisterKingdomIllegalName() {
    final String username = "Username";
    login(username);
    final String kingdomName = "cool %%^$^ another one";
    final RegisterKingdomForm form = new RegisterKingdomForm(kingdomName);

    try {
      client.post(form, REGISTER_KINGDOM);
    } catch (HttpClientErrorException e) {
      assertResponseIsBadRequest(e);
      final ErrorResponse errorResponse = getErrorResponse(e);
      assertEquals("name", errorResponse.getData().get("field"));
    }

  }

  @Test
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
    }
  }

  @Test
  public void testDeleteKingdomUnauthorized() {
    try {
      client.delete(DELETE_KINGDOM);
    } catch (HttpClientErrorException e) {
      assertResponseIsForbidden(e);
    }
  }

  @Test
  public void testDeleteKingdomAuthorized() {
    final String username = "cool_username";
    login(username);
    final String kingdomName = "cool fine kingdom";
    final RegisterKingdomForm form = new RegisterKingdomForm(kingdomName);
    final ResponseEntity response = client.post(form, REGISTER_KINGDOM);
    assertResponseIsCreated(response);

    final ResponseEntity kingdomResponse = client.get(GET_KINGDOM);
    final KingdomDto dto = JsonUtils.jsonToObject((String) kingdomResponse.getBody(), KingdomDto.class);

    assertTrue(dto.getName() == kingdomName);
    assertTrue(dto.getOwner() == username);
    assertTrue(dto.getCreatedAt() != null);

    client.delete(DELETE_KINGDOM);

    try {
      client.get(GET_KINGDOM);
    } catch (HttpClientErrorException e) {
      assertResponseIsNotFound(e);
    }

  }

  private ErrorResponse getErrorResponse(final HttpClientErrorException errorException) {
    return JsonUtils.jsonToObject(errorException.getResponseBodyAsString(), ErrorResponse.class);
  }

}
