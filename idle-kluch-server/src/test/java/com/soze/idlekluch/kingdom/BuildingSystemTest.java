package com.soze.idlekluch.kingdom;

import com.soze.idlekluch.BaseAuthTest;
import com.soze.idlekluch.kingdom.dto.BuildBuildingForm;
import com.soze.idlekluch.kingdom.dto.BuildingDefinitionDto;
import com.soze.idlekluch.kingdom.dto.BuildingDto;
import com.soze.idlekluch.kingdom.dto.RegisterKingdomForm;
import com.soze.idlekluch.routes.Routes;
import com.soze.idlekluch.utils.JsonUtils;
import com.soze.idlekluch.utils.sql.DatabaseReset;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.soze.idlekluch.utils.http.ResponseAssertUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BuildingSystemTest extends BaseAuthTest {

  @Before
  public void setup() {
    super.setup();
    DatabaseReset.resetDatabase();
  }

  @Test
  public void testGetAllBuildings() {
    final ResponseEntity response = client.get(Routes.BUILDING_GET_ALL);
    final List<BuildingDefinitionDto> dtos = JsonUtils.jsonToList((String) response.getBody(), BuildingDefinitionDto.class);
    System.out.println(dtos);

    assertTrue(dtos != null);
    assertTrue(dtos.size() > 0);
  }

  @Test
  public void testPlaceBuildingUnauthorized() {
    assertResponseIsForbidden(() -> client.post(new BuildBuildingForm("1", 1, 1), Routes.BUILDING_BUILD));
  }

  @Test
  public void testPlaceBuildingInvalidBuildingId() {
    login("kingdomowner");
    final RegisterKingdomForm registerKingdomForm = new RegisterKingdomForm("kingdom");
    client.post(registerKingdomForm, Routes.KINGDOM_CREATE);

    final BuildBuildingForm form = new BuildBuildingForm("-5", 5, 5);
    assertResponseIsBadRequest(() -> client.post(form, Routes.BUILDING_BUILD));
  }

  @Test
  public void testPlaceBuildingInvalidPlace() {
    login("kingdomowner");
    final RegisterKingdomForm registerKingdomForm = new RegisterKingdomForm("kingdom");
    client.post(registerKingdomForm, Routes.KINGDOM_CREATE);

    final BuildBuildingForm form = new BuildBuildingForm("1", -500000, 5);
    assertResponseIsBadRequest(() -> client.post(form, Routes.BUILDING_BUILD));
  }

  @Test
  public void testPlaceBuilding() {
    login("kingdomowner");
    final RegisterKingdomForm registerKingdomForm = new RegisterKingdomForm("kingdom");
    client.post(registerKingdomForm, Routes.KINGDOM_CREATE);

    final BuildBuildingForm form = new BuildBuildingForm("1", 150, 150);
    final ResponseEntity buildResponse = client.post(form, Routes.BUILDING_BUILD);
    assertResponseIsCreated(buildResponse);

    final ResponseEntity response = client.get(Routes.BUILDING_OWN);
    final List<BuildingDto> dtos = JsonUtils.jsonToList((String)response.getBody(), BuildingDto.class);
    assertTrue(!dtos.isEmpty());
    assertEquals(1, dtos.size());
  }

  @Test
  public void testPlaceTwoBuildings() {
    login("kingdomowner");
    final RegisterKingdomForm registerKingdomForm = new RegisterKingdomForm("kingdom");
    client.post(registerKingdomForm, Routes.KINGDOM_CREATE);

    final BuildBuildingForm form = new BuildBuildingForm("1", 150, 150);
    final ResponseEntity buildResponse = client.post(form, Routes.BUILDING_BUILD);
    assertResponseIsCreated(buildResponse);

    final BuildBuildingForm anotherForm = new BuildBuildingForm("0", 250, 250);
    final ResponseEntity anotherBuildResponse = client.post(anotherForm, Routes.BUILDING_BUILD);
    assertResponseIsCreated(anotherBuildResponse);

    final ResponseEntity response = client.get(Routes.BUILDING_OWN);
    final List<BuildingDto> dtos = JsonUtils.jsonToList((String)response.getBody(), BuildingDto.class);
    assertTrue(!dtos.isEmpty());
    assertEquals(2, dtos.size());
  }

  @Test
  public void testGetOwnBuildingsUnauthorized() {
    assertResponseIsForbidden(() -> client.get(Routes.BUILDING_OWN));
  }

}
