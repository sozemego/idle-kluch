package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.IntAuthTest;
import com.soze.idlekluch.RootConfig;
import com.soze.idlekluch.game.engine.nodes.Nodes;
import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.exception.BuildingDoesNotExistException;
import com.soze.idlekluch.kingdom.exception.CannotAffordBuildingException;
import com.soze.idlekluch.kingdom.exception.UserDoesNotHaveKingdomException;
import com.soze.idlekluch.user.exception.AuthUserDoesNotExistException;
import com.soze.idlekluch.utils.CommonUtils;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.idlekluch.utils.jpa.InvalidUUIDException;
import com.soze.idlekluch.utils.sql.DatabaseReset;
import com.soze.klecs.entity.Entity;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;
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

  @Autowired
  private BuildingService buildingService;

  @Autowired
  private GameEngine gameEngine;

  @Autowired
  private KingdomService kingdomService;

  @BeforeClass
  public static void beforeClass() {
    DatabaseReset.resetDatabase();
  }

  @Test(expected = AuthUserDoesNotExistException.class)
  public void testBuildBuildingUserDoesNotExist() throws Exception {
    buildingService.buildBuilding("dontexist", new BuildBuildingForm("1", 15, 15));
  }

  @Test(expected = UserDoesNotHaveKingdomException.class)
  public void testBuildingBuildingNoKingdom() throws Exception {
    final String username = CommonUtils.generateRandomString(15);
    register(username);
    final BuildBuildingForm form = new BuildBuildingForm("1", 15, 15);
    buildingService.buildBuilding(username, form);
  }

  @Test
  public void testBuildBuilding() throws Exception {
    final String username = CommonUtils.generateRandomString(15);
    register(username);
    createKingdom(username, CommonUtils.generateRandomString(15));
    final Kingdom kingdom = kingdomService.getUsersKingdom(username).get();

    final BuildBuildingForm form = new BuildBuildingForm(
        SMALL_WAREHOUSE_ID, 15, 15
    );

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
    register(username);
    createKingdom(username, CommonUtils.generateRandomString(15));

    final BuildBuildingForm form = new BuildBuildingForm(
        "AGEBAGE@!$%$$", 15, 15
    );

    buildingService.buildBuilding(username, form);
  }

  @Test(expected = BuildingDoesNotExistException.class)
  public void testBuildBuildingDoesNotExist() throws Exception {
    final String username = CommonUtils.generateRandomString(15);
    register(username);
    createKingdom(username, CommonUtils.generateRandomString(15));

    final BuildBuildingForm form = new BuildBuildingForm(
        EntityUUID.randomId().toString(), 15, 15
    );

    buildingService.buildBuilding(username, form);
  }

  @Test(expected = CannotAffordBuildingException.class)
  public void testBuildBuildingCannotAfford() {
    final String username = "poorguy" + CommonUtils.generateRandomString(12);
    register(username);
    final String kingdomName = "poorkingdom" + CommonUtils.generateRandomString(12);
    createKingdom(username, kingdomName);
    final Kingdom kingdom = kingdomService.getUsersKingdom(username).get();
    kingdom.setIdleBucks(5);
    kingdomService.updateKingdom(kingdom);

    final BuildBuildingForm form = new BuildBuildingForm(
        SMALL_WAREHOUSE_ID, 15, 15
    );

    buildingService.buildBuilding(username, form);
  }

  @Test
  @Ignore("This test takes a while to execute but will fail - will be reenabled when the functionality is there")
  public void testBuildManyBuildingsAtOnceCannotCheatCost() {
    final String username = CommonUtils.generateRandomString(12);
    register(username);
    final String kingdomName = CommonUtils.generateRandomString(12);
    createKingdom(username, kingdomName);

    final Kingdom kingdom = kingdomService.getUsersKingdom(username).get();
    kingdom.setIdleBucks(100 * 50); //100 is a cost of a small warehouse so this kingdom should afford 500 of them
    kingdomService.updateKingdom(kingdom);

    final List<Thread> threads = new ArrayList<>();

    for(int i = 0; i < 5; i++) {
      final Thread thread = new Thread(() -> {
        for(int j = 0; j < 20; j++) {
          try {
            final BuildBuildingForm form = new BuildBuildingForm(
                SMALL_WAREHOUSE_ID, 15, 15
            );

            buildingService.buildBuilding(username, form);
          } catch (CannotAffordBuildingException e) {

          }
        }
      });
      threads.add(thread);
      thread.start();
    }

    threads.forEach(t -> {
      try {
        t.join();
      } catch (InterruptedException e) {

      }
    });

    //after all threads have run, there should be only 500 buildings/entities for this kingdom
    final List<PersistentEntity> kingdomBuildings = buildingService
                                                        .getAllConstructedBuildings()
                                                        .stream()
                                                        .filter(building -> building.getOwnershipComponent().getOwnerId().equals(kingdom.getKingdomId()))
                                                        .collect(Collectors.toList());

    assertEquals(0, kingdomService.getUsersKingdom(username).get().getIdleBucks());
    assertEquals(50, kingdomBuildings.size());
  }


}