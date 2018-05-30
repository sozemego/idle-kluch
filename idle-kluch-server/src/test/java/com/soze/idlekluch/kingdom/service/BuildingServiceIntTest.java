package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.IntAuthTest;
import com.soze.idlekluch.RootConfig;
import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.exception.BuildingDoesNotExistException;
import com.soze.idlekluch.kingdom.exception.CannotAffordBuildingException;
import com.soze.idlekluch.kingdom.exception.SpaceAlreadyOccupiedException;
import com.soze.idlekluch.kingdom.exception.UserDoesNotHaveKingdomException;
import com.soze.idlekluch.user.exception.AuthUserDoesNotExistException;
import com.soze.idlekluch.user.exception.NotAuthenticatedException;
import com.soze.idlekluch.utils.CommonUtils;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.idlekluch.utils.jpa.InvalidUUIDException;
import com.soze.idlekluch.utils.sql.DatabaseReset;
import com.soze.idlekluch.world.entity.TileId;
import com.soze.idlekluch.world.service.WorldService;
import com.soze.idlekluch.world.utils.WorldUtils;
import com.soze.klecs.entity.Entity;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(
    classes = {
        RootConfig.class
    }
)
@WebAppConfiguration
@ActiveProfiles({"integration-test"})
public class BuildingServiceIntTest extends IntAuthTest {

  private static final String SMALL_WAREHOUSE_ID = "7a4df465-b4c3-4e9f-854a-248988220dfb";
  private static final String WAREHOUSE_ID = "4517e8b9-de2e-473d-98e8-4c6c73c46c4d";

  private static final List<Point> AVAILABLE_BUILDING_POSITIONS = new ArrayList<>();
  private static final int SEPARATION = 500;
  private static int CURRENT_BUILDING_POSITION = 0;

  {
    for(int i = 0; i < 2500; i++) {
      AVAILABLE_BUILDING_POSITIONS.add(new Point(i * SEPARATION, i * SEPARATION));
    }
  }

  @Autowired
  private BuildingService buildingService;

  @Autowired
  private GameEngine gameEngine;

  @Autowired
  private KingdomService kingdomService;

  @Autowired
  private WorldService worldService;

  @BeforeClass
  public static void beforeClass() {
    DatabaseReset.resetDatabase();
    for (int i = 0; i < 250; i++) {
      AVAILABLE_BUILDING_POSITIONS.add(new Point(i * SEPARATION, i * SEPARATION));
    }
  }

  @Test(expected = NotAuthenticatedException.class)
  public void testBuildBuildingUserDoesNotExist() throws Exception {
    buildingService.buildBuilding("dontexist", new BuildBuildingForm(UUID.randomUUID().toString(), "1", 15, 15));
  }

  @Test(expected = UserDoesNotHaveKingdomException.class)
  public void testBuildingBuildingNoKingdom() throws Exception {
    final String username = CommonUtils.generateRandomString(15);
    register(username);
    final BuildBuildingForm form = new BuildBuildingForm(UUID.randomUUID().toString(), SMALL_WAREHOUSE_ID, 15, 15);
    buildingService.buildBuilding(username, form);
  }

  @Test
  public void testBuildBuilding() throws Exception {
    final String username = CommonUtils.generateRandomString(15);
    createKingdom(username, CommonUtils.generateRandomString(15));
    final Kingdom kingdom = kingdomService.getUsersKingdom(username).get();

    final Point buildingPosition = nextBuildingPosition();
    final BuildBuildingForm form = new BuildBuildingForm(
        UUID.randomUUID().toString(), SMALL_WAREHOUSE_ID, buildingPosition.x, buildingPosition.y
    );

    worldService.createWorldChunk(WorldUtils.translateCoordinates(buildingPosition.x, buildingPosition.y));

    final List<Entity> buildings = buildingService.getOwnBuildings(username);
    final long kingdomIdleBucksBefore = kingdom.getIdleBucks();
    buildingService.buildBuilding(username, form);
    gameEngine.update(0.25f);
    assertTrue(buildings.size() < buildingService.getOwnBuildings(username).size());
    assertTrue(kingdomIdleBucksBefore > kingdomService.getUsersKingdom(username).get().getIdleBucks());
  }

  @Test(expected = InvalidUUIDException.class)
  public void testBuildBuildingInvalidUUID() throws Exception {
    final String username = CommonUtils.generateRandomString(15);
    createKingdom(username, CommonUtils.generateRandomString(15));

    final BuildBuildingForm form = new BuildBuildingForm(
        UUID.randomUUID().toString(),"AGEBAGE@!$%$$", 15, 15
    );

    buildingService.buildBuilding(username, form);
  }

  @Test(expected = BuildingDoesNotExistException.class)
  public void testBuildBuildingDoesNotExist() throws Exception {
    final String username = CommonUtils.generateRandomString(15);
    createKingdom(username, CommonUtils.generateRandomString(15));

    final BuildBuildingForm form = new BuildBuildingForm(
        UUID.randomUUID().toString(), EntityUUID.randomId().toString(), 15, 15
    );

    buildingService.buildBuilding(username, form);
  }

  @Test(expected = CannotAffordBuildingException.class)
  public void testBuildBuildingCannotAfford() {
    final String username = "poorguy" + CommonUtils.generateRandomString(12);
    final String kingdomName = "poorkingdom" + CommonUtils.generateRandomString(12);
    createKingdom(username, kingdomName);
    final Kingdom kingdom = kingdomService.getUsersKingdom(username).get();
    kingdom.setIdleBucks(5);
    kingdomService.updateKingdom(kingdom);

    final BuildBuildingForm form = new BuildBuildingForm(
        UUID.randomUUID().toString(), SMALL_WAREHOUSE_ID, 15, 15
    );

    buildingService.buildBuilding(username, form);
  }

  @Test
  public void testBuildManyBuildingsAtOnceCannotCheatCost() {
    final String username = CommonUtils.generateRandomString(12);
    final String kingdomName = CommonUtils.generateRandomString(12);
    createKingdom(username, kingdomName);

    final Kingdom kingdom = kingdomService.getUsersKingdom(username).get();
    kingdom.setIdleBucks(100 * 50); //100 is a cost of a small warehouse so this kingdom should afford 50 of them
    kingdomService.updateKingdom(kingdom);

    final List<Thread> threads = new ArrayList<>();

    for(int i = 0; i < 5; i++) {
      final Thread thread = new Thread(() -> {
        for(int j = 0; j < 20; j++) {
          try {
            final Point point = nextBuildingPosition();
            worldService.createWorldChunk(WorldUtils.translateCoordinates(point.x, point.y));
            final BuildBuildingForm form = new BuildBuildingForm(
                UUID.randomUUID().toString(), SMALL_WAREHOUSE_ID, point.x, point.y
            );

            buildingService.buildBuilding(username, form);
          } catch (CannotAffordBuildingException e) {

          }
        }
      });
      threads.add(thread);
    }

    threads.forEach(Thread::start);
    threads.forEach(t -> {
      try {
        t.join();
      } catch (InterruptedException e) {

      }
    });

    //after all threads have run, there should be only 50 buildings/entities for this kingdom
    final List<PersistentEntity> kingdomBuildings = buildingService
                                                        .getAllConstructedBuildings()
                                                        .stream()
                                                        .filter(building -> building.getOwnershipComponent().getOwnerId().equals(kingdom.getKingdomId()))
                                                        .collect(Collectors.toList());

    assertEquals(0, kingdomService.getUsersKingdom(username).get().getIdleBucks());
    assertEquals(50, kingdomBuildings.size());
  }

  @Test(expected = SpaceAlreadyOccupiedException.class)
  public void testCannotPlaceTwoBuildingsAtTheSameSpot() {
    final String username = CommonUtils.generateRandomString(12);
    final String kingdomName = CommonUtils.generateRandomString(12);
    createKingdom(username, kingdomName, 2500);

    final Point buildingPosition = nextBuildingPosition();
    final BuildBuildingForm form = new BuildBuildingForm(
        UUID.randomUUID().toString(), SMALL_WAREHOUSE_ID, buildingPosition.x, buildingPosition.y
    );

    buildingService.buildBuilding(username, form);
    buildingService.buildBuilding(username, form);
  }

  private synchronized Point nextBuildingPosition() {
    return AVAILABLE_BUILDING_POSITIONS.get(CURRENT_BUILDING_POSITION++);
  }

}