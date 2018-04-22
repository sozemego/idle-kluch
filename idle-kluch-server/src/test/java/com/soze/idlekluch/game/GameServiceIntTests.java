package com.soze.idlekluch.game;

import com.soze.idlekluch.RootConfig;
import com.soze.idlekluch.WebApplicationInitializer;
import com.soze.idlekluch.game.message.WorldChunkMessage;
import com.soze.idlekluch.game.service.GameService;
import com.soze.idlekluch.game.service.WebSocketMessagingService;
import com.soze.idlekluch.game.service.WebSocketMessagingServiceTest;
import com.soze.idlekluch.routes.Routes;
import com.soze.idlekluch.utils.JsonUtils;
import com.soze.idlekluch.utils.sql.DatabaseReset;
import com.soze.idlekluch.world.service.World;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration(
  classes = {
    RootConfig.class
//    WebApplicationInitializer.class
  }
)
@WebAppConfiguration
@ActiveProfiles({"integration-test"})
public class GameServiceIntTests {

  @Autowired
  private GameService gameService;

  @Autowired
  private WebSocketMessagingServiceTest messagingService;

  @Before
  public void setup() {
    DatabaseReset.resetDatabase();
  }

  @Test
  public void testInitMessage() throws Exception {
    gameService.handleInitMessage("username");
    final Object[] message = messagingService.getUserMessages().get(0);
    assertEquals("username", message[0]);
    assertEquals(Routes.GAME + Routes.GAME_OUTBOUND, message[1]);
    final WorldChunkMessage worldChunkMessage = JsonUtils.jsonToObject((String) message[2], WorldChunkMessage.class);
    assertFalse(worldChunkMessage.getTiles().isEmpty());
  }

}
