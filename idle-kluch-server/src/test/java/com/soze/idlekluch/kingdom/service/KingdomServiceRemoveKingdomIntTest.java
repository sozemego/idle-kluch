package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.IntAuthTest;
import com.soze.idlekluch.RootConfig;
import com.soze.idlekluch.core.utils.CommonUtils;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.core.utils.sql.DatabaseReset;
import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.kingdom.exception.UserDoesNotHaveKingdomException;
import com.soze.idlekluch.world.service.WorldService;
import com.soze.idlekluch.world.utils.WorldUtils;
import com.soze.klecs.entity.Entity;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@ContextConfiguration(
  classes = {
    RootConfig.class
  }
)
@WebAppConfiguration
@ActiveProfiles({"integration-test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class KingdomServiceRemoveKingdomIntTest extends IntAuthTest {

  private static final String WAREHOUSE_ID = "4517e8b9-de2e-473d-98e8-4c6c73c46c4d";

  @Autowired
  private KingdomService kingdomService;

  @Autowired
  private BuildingService buildingService;

  @Autowired
  private WorldService worldService;

  @Autowired
  private GameEngine gameEngine;

  @BeforeClass
  public static void beforeClass() {
    DatabaseReset.deleteData();
  }

  @Before
  public void setup() {
    gameEngine.reset();
    DatabaseReset.deleteData();
  }

  @Test
  public void testRemoveKingdom() {
    final String kingdomName = CommonUtils.generateRandomString(12);
    final String username = CommonUtils.generateRandomString(12);
    createKingdom(username, kingdomName);
    assertTrue(kingdomService.getUsersKingdom(username).isPresent());

    kingdomService.removeKingdom(username);
    assertFalse(kingdomService.getUsersKingdom(username).isPresent());
  }

  @Test(expected = UserDoesNotHaveKingdomException.class)
  public void testRemoveKingdomDoesNotExist() {
    final String username = CommonUtils.generateRandomString(12);
    kingdomService.removeKingdom(username);
  }

  @Test(expected = UserDoesNotHaveKingdomException.class)
  public void testRemoveKingdomOnceAddedThenRemoved() {
    final String kingdomName = CommonUtils.generateRandomString(12);
    final String username = CommonUtils.generateRandomString(12);
    createKingdom(username, kingdomName);

    kingdomService.removeKingdom(username);
    kingdomService.removeKingdom(username);
  }

  @Test
  public void testAddKingdomOnceRemoved() {
    final String kingdomName = CommonUtils.generateRandomString(12);
    final String username = CommonUtils.generateRandomString(12);
    createKingdom(username, kingdomName);

    assertTrue(kingdomService.getUsersKingdom(username).isPresent());
    kingdomService.removeKingdom(username);
    assertFalse(kingdomService.getUsersKingdom(username).isPresent());
    createKingdom(username, kingdomName);
    assertTrue(kingdomService.getUsersKingdom(username).isPresent());
  }

  @Test
  public void testRemoveSameKingdomManyTimes() throws Exception {
    final String username = CommonUtils.generateRandomString(12);
    final String anotherUsername = CommonUtils.generateRandomString(12);
    createKingdom(username, CommonUtils.generateRandomString(12));
    createKingdom(anotherUsername, CommonUtils.generateRandomString(12));

    final List<Thread> threads = new ArrayList<>();
    final List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

    for (int i = 0; i < 5; i++) {
      final Thread thread = new Thread(() -> {
        for (int j = 0; j < 20; j++) {
          try {
            kingdomService.removeKingdom(username);
          } catch (UserDoesNotHaveKingdomException e) {
            exceptions.add(e);
          }
        }
      });
      threads.add(thread);
      thread.start();

      final Thread anotherThread = new Thread(() -> {
        for (int j = 0; j < 20; j++) {
          try {
            kingdomService.removeKingdom(anotherUsername);
          } catch (UserDoesNotHaveKingdomException e) {
            exceptions.add(e);
          }
        }
      });
      threads.add(anotherThread);
      anotherThread.start();
    }

    for (final Thread thread : threads) {
      thread.join();
    }

    assertEquals(((5 * 20) * 2) - 2, exceptions.size());
  }

  @Test
  public void testRemoveKingdomShouldRemoveBuilding() {
    final String username = CommonUtils.generateRandomString(12);
    final String kingdomName = CommonUtils.generateRandomString(12);
    createKingdom(username, kingdomName);

    final Point buildingPosition = new Point(0, 0);

    worldService.createWorldChunk(WorldUtils.translateCoordinates(buildingPosition.x, buildingPosition.y));
    buildingService.buildBuilding(username, new BuildBuildingForm(EntityUUID.randomId().toString(), WAREHOUSE_ID, buildingPosition.x, buildingPosition.y));

    final List<Entity> buildings = buildingService.getOwnBuildings(username);
    assertEquals(1, buildings.size());
    assertEquals(1, buildingService.getAllConstructedBuildings().size());

    kingdomService.removeKingdom(username);
    createKingdom(username, kingdomName);
    final List<Entity> buildingsAfterKingdomRemove = buildingService.getOwnBuildings(username);
    assertEquals(0, buildingsAfterKingdomRemove.size());
    assertEquals(0, buildingService.getAllConstructedBuildings().size());
  }

  @Test
  public void testRemoveKingdomShouldRemoveBuildings() {
    final String username = CommonUtils.generateRandomString(12);
    final String kingdomName = CommonUtils.generateRandomString(12);
    createKingdom(username, kingdomName, 5000);

    final Point buildingPosition1 = new Point(0, 0);
    final Point buildingPosition2 = new Point(200, 100);
    final Point buildingPosition3 = new Point(400, 200);
    final Point buildingPosition4 = new Point(600, 300);
    final Point buildingPosition5 = new Point(800, 400);

    worldService.createWorldChunk(WorldUtils.translateCoordinates(buildingPosition1.x, buildingPosition1.y));
    worldService.createWorldChunk(WorldUtils.translateCoordinates(buildingPosition2.x, buildingPosition2.y));
    worldService.createWorldChunk(WorldUtils.translateCoordinates(buildingPosition3.x, buildingPosition3.y));
    worldService.createWorldChunk(WorldUtils.translateCoordinates(buildingPosition4.x, buildingPosition4.y));
    worldService.createWorldChunk(WorldUtils.translateCoordinates(buildingPosition5.x, buildingPosition5.y));
    buildingService.buildBuilding(username, new BuildBuildingForm(EntityUUID.randomId().toString(), WAREHOUSE_ID, buildingPosition1.x, buildingPosition1.y));
    buildingService.buildBuilding(username, new BuildBuildingForm(EntityUUID.randomId().toString(), WAREHOUSE_ID, buildingPosition2.x, buildingPosition2.y));
    buildingService.buildBuilding(username, new BuildBuildingForm(EntityUUID.randomId().toString(), WAREHOUSE_ID, buildingPosition3.x, buildingPosition3.y));
    buildingService.buildBuilding(username, new BuildBuildingForm(EntityUUID.randomId().toString(), WAREHOUSE_ID, buildingPosition4.x, buildingPosition4.y));
    buildingService.buildBuilding(username, new BuildBuildingForm(EntityUUID.randomId().toString(), WAREHOUSE_ID, buildingPosition5.x, buildingPosition5.y));

    final List<Entity> buildings = buildingService.getOwnBuildings(username);
    assertEquals(5, buildings.size());
    assertEquals(5, buildingService.getAllConstructedBuildings().size());

    kingdomService.removeKingdom(username);
    createKingdom(username, kingdomName);
    final List<Entity> buildingsAfterKingdomRemove = buildingService.getOwnBuildings(username);
    assertEquals(0, buildingsAfterKingdomRemove.size());
    assertEquals(0, buildingService.getAllConstructedBuildings().size());
  }


}
