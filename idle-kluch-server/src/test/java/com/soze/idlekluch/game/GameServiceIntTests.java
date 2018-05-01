package com.soze.idlekluch.game;

import com.soze.idlekluch.IntAuthTest;
import com.soze.idlekluch.RootConfig;
import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.game.service.GameService;
import com.soze.idlekluch.game.service.WebSocketMessagingServiceTest;
import com.soze.idlekluch.kingdom.exception.BuildingDoesNotExistException;
import com.soze.idlekluch.kingdom.exception.UserDoesNotHaveKingdomException;
import com.soze.idlekluch.kingdom.service.BuildingService;
import com.soze.idlekluch.user.exception.AuthUserDoesNotExistException;
import com.soze.idlekluch.utils.CommonUtils;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.idlekluch.utils.jpa.InvalidUUIDException;
import com.soze.idlekluch.utils.sql.DatabaseReset;
import com.soze.klecs.entity.Entity;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration(
  classes = {
    RootConfig.class
  }
)
@WebAppConfiguration
@ActiveProfiles({"integration-test"})
public class GameServiceIntTests extends IntAuthTest {

  private static final String SMALL_WAREHOUSE_ID = "7a4df465-b4c3-4e9f-854a-248988220dfb";
  private static final String WAREHOUSE_ID = "4517e8b9-de2e-473d-98e8-4c6c73c46c4d";

  @Autowired
  private GameService gameService;

  @Autowired
  private GameEngine gameEngine;

  @Autowired
  private WebSocketMessagingServiceTest messagingService;

  @Autowired
  private BuildingService buildingService;

  @Before
  public void setup() {
    messagingService.clearMessages();
  }

  @BeforeClass
  public static void beforeClass() {
    DatabaseReset.resetDatabase();
  }

//  @Test
//  public void testInitMessage() throws Exception {
//    gameService.handleInitMessage("username");
//    final Object[] message = messagingService.getUserMessages().get(0);
//    assertEquals("username", message[0]);
//    assertEquals(Routes.GAME + Routes.GAME_OUTBOUND, message[1]);
//    final WorldChunkMessage worldChunkMessage = JsonUtils.jsonToObject((String) message[2], WorldChunkMessage.class);
//    assertFalse(worldChunkMessage.getTiles().isEmpty());
//  }

  @Test(expected = AuthUserDoesNotExistException.class)
  public void testBuildBuildingUserDoesNotExist() throws Exception {
    gameService.handleBuildBuildingMessage("dontexist", new BuildBuildingForm("1", 15, 15));
  }

  @Test(expected = UserDoesNotHaveKingdomException.class)
  public void testBuildingBuildingNoKingdom() throws Exception {
    final String username = CommonUtils.generateRandomString(15);
    register(username);
    final BuildBuildingForm form = new BuildBuildingForm("1", 15, 15);
    gameService.handleBuildBuildingMessage(username, form);
  }

  @Test
  public void testBuildBuilding() throws Exception {
    final String username = CommonUtils.generateRandomString(15);
    register(username);
    createKingdom(username, CommonUtils.generateRandomString(15));

    final BuildBuildingForm form = new BuildBuildingForm(
      SMALL_WAREHOUSE_ID, 15, 15
    );

    final List<Entity> buildings = buildingService.getOwnBuildings(username);
    gameService.handleBuildBuildingMessage(username, form);
    gameEngine.update(0.25f);
    assertTrue(buildings.size() < buildingService.getOwnBuildings(username).size());
  }

  @Test(expected = InvalidUUIDException.class)
  public void testBuildBuildingInvalidUUID() throws Exception {
    final String username = CommonUtils.generateRandomString(15);
    register(username);
    createKingdom(username, CommonUtils.generateRandomString(15));

    final BuildBuildingForm form = new BuildBuildingForm(
      "AGEBAGE@!$%$$", 15, 15
    );

    gameService.handleBuildBuildingMessage(username, form);
  }

  @Test(expected = BuildingDoesNotExistException.class)
  public void testBuildBuildingDoesNotExist() throws Exception {
    final String username = CommonUtils.generateRandomString(15);
    register(username);
    createKingdom(username, CommonUtils.generateRandomString(15));

    final BuildBuildingForm form = new BuildBuildingForm(
      EntityUUID.randomId().toString(), 15, 15
    );

    gameService.handleBuildBuildingMessage(username, form);
  }

}
