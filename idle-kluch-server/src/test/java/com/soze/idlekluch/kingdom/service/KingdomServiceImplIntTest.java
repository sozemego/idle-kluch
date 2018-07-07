package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.IntAuthTest;
import com.soze.idlekluch.RootConfig;
import com.soze.idlekluch.core.exception.EntityAlreadyExistsException;
import com.soze.idlekluch.core.exception.InvalidFormException;
import com.soze.idlekluch.core.utils.CommonUtils;
import com.soze.idlekluch.core.utils.sql.DatabaseReset;
import com.soze.idlekluch.kingdom.dto.RegisterKingdomForm;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.exception.UserAlreadyHasKingdomException;
import com.soze.klecs.entity.Entity;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration(
  classes = {
    RootConfig.class
  }
)
@WebAppConfiguration
@ActiveProfiles({"integration-test"})
public class KingdomServiceImplIntTest extends IntAuthTest {

  @Autowired
  private KingdomService kingdomService;

  @Autowired
  private BuildingService buildingService;

  @BeforeClass
  public static void setup() {
    DatabaseReset.deleteData();
  }

  @Test
  public void testRegisterKingdom() {
    final String username = CommonUtils.generateRandomString(12);
    register(username);
    final String kingdomName = "cool_kingdom";

    kingdomService.addKingdom(username, new RegisterKingdomForm(kingdomName));

    final Kingdom kingdom = kingdomService.getKingdom(kingdomName).get();

    assertTrue(kingdom.getName().equals(kingdomName));
    assertTrue(kingdom.getOwner().getUsername().equals(username));
    assertTrue(kingdom.getCreatedAt() != null);
  }

  @Test
  public void testRegisterKingdomShouldHaveStartingBuilding() {
    final String username = CommonUtils.generateRandomString(12);
    register(username);
    final String kingdomName = "great_kingdom";

    kingdomService.addKingdom(username, new RegisterKingdomForm(kingdomName));

    final List<Entity> buildings = buildingService.getOwnBuildings(username);
    assertEquals(1, buildings.size());
  }

  @Test
  public void testGetKingdomByUsername() {
    final String username = CommonUtils.generateRandomString(12);
    register(username);
    final String kingdomName = CommonUtils.generateRandomString(12);

    kingdomService.addKingdom(username, new RegisterKingdomForm(kingdomName));

    final Kingdom kingdom = kingdomService.getUsersKingdom(username).get();

    assertTrue(kingdom.getName().equals(kingdomName));
    assertTrue(kingdom.getOwner().getUsername().equals(username));
    assertTrue(kingdom.getCreatedAt() != null);
  }

  @Test(expected = InvalidFormException.class)
  public void testIllegalKingdomName() {
    final String username = CommonUtils.generateRandomString(12);
    register(username);
    final String kingdomName = "cool_%%^$^_another_one";

    kingdomService.addKingdom(username, new RegisterKingdomForm(kingdomName));
  }

  @Test(expected = InvalidFormException.class)
  public void testKingdomNameTooLong() {
    final String username = CommonUtils.generateRandomString(12);
    register(username);
    final String kingdomName = CommonUtils.generateRandomString(500);

    kingdomService.addKingdom(username, new RegisterKingdomForm(kingdomName));
  }

  @Test(expected = EntityAlreadyExistsException.class)
  public void testCreateKingdomAlreadyExists() {
    final String username = CommonUtils.generateRandomString(12);
    register(username);
    final String kingdomName = CommonUtils.generateRandomString(12);
    kingdomService.addKingdom(username, new RegisterKingdomForm(kingdomName));

    final String anotherUsername = CommonUtils.generateRandomString(12);
    register(anotherUsername);
    kingdomService.addKingdom(anotherUsername, new RegisterKingdomForm(kingdomName));
  }

  @Test(expected = UserAlreadyHasKingdomException.class)
  public void testCreateKingdomUserAlreadyHasKingdom() {
    final String username = CommonUtils.generateRandomString(12);
    register(username);
    final String kingdomName = CommonUtils.generateRandomString(12);
    kingdomService.addKingdom(username, new RegisterKingdomForm(kingdomName));
    kingdomService.addKingdom(username, new RegisterKingdomForm(CommonUtils.generateRandomString(12)));
  }





}