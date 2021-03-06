package com.soze.idlekluch.game.engine.systems;

import com.soze.idlekluch.RootConfig;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.core.utils.sql.DatabaseReset;
import com.soze.idlekluch.game.engine.components.NameComponent;
import com.soze.idlekluch.game.engine.components.ResourceStorageComponent;
import com.soze.idlekluch.game.engine.components.resourceharvester.HarvestingProgress;
import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceHarvesterComponent;
import com.soze.idlekluch.game.message.StartHarvestingMessage;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.game.service.WebSocketMessagingServiceTest;
import com.soze.idlekluch.world.service.ResourceService;
import com.soze.klecs.entity.Entity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

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
@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
public class ResourceHarvesterSystemTest {

  @Autowired
  private GameEngine gameEngine;

  @Autowired
  private ResourceService resourceService;

  @Autowired
  private WebSocketMessagingServiceTest webSocketMessagingServiceTest;

  @Before
  public void setup() {
    DatabaseReset.deleteData();
    webSocketMessagingServiceTest.clearMessages();
    gameEngine.reset();
  }

  @Test
  public void testResourceHarvesterSystem() {
    Entity entity = gameEngine.createEmptyEntity(EntityUUID.randomId());

    NameComponent name = new NameComponent("Harvester");
    entity.addComponent(name);

    ResourceHarvesterComponent resourceHarvesterComponent = new ResourceHarvesterComponent();
    resourceHarvesterComponent.setUnitsPerMinute(1);
    resourceHarvesterComponent.setResourceId(resourceService.getResource("Wood").get().getResourceId());
    entity.addComponent(resourceHarvesterComponent);

    ResourceStorageComponent resourceStorageComponent = new ResourceStorageComponent(40, 30);
    entity.addComponent(resourceStorageComponent);

    gameEngine.addEntity(entity);

    assertEquals(HarvestingProgress.HarvestingState.WAITING, resourceHarvesterComponent.getHarvestingProgress().getHarvestingState());
    assertEquals(0f, resourceHarvesterComponent.getHarvestingProgress().getHarvestingProgressPercent(), 0f);
    assertEquals(0, resourceStorageComponent.getResources().size());
    assertEquals(0, webSocketMessagingServiceTest.getBroadcastMessages(StartHarvestingMessage.class).size());

    //pass time by 30 seconds
    gameEngine.update(30f);

    assertEquals(HarvestingProgress.HarvestingState.HARVESTING, resourceHarvesterComponent.getHarvestingProgress().getHarvestingState());
    assertEquals(0.5f, resourceHarvesterComponent.getHarvestingProgress().getHarvestingProgressPercent(), 0f);
    assertEquals(0, resourceStorageComponent.getResources().size());
    assertEquals(1, webSocketMessagingServiceTest.getBroadcastMessages(StartHarvestingMessage.class).size());

    gameEngine.update(30f);

    assertEquals(HarvestingProgress.HarvestingState.WAITING, resourceHarvesterComponent.getHarvestingProgress().getHarvestingState());
    assertEquals(0f, resourceHarvesterComponent.getHarvestingProgress().getHarvestingProgressPercent(), 0f);
    assertEquals(1, resourceStorageComponent.getResources().size());
    assertTrue(resourceStorageComponent.getResources().get(0).equals(resourceService.getResource("Wood").get()));
    assertEquals(1, webSocketMessagingServiceTest.getBroadcastMessages(StartHarvestingMessage.class).size());
  }

  @Test
  public void testResourceHarvestingSystemNoCapacity() {
    Entity entity = gameEngine.createEmptyEntity(EntityUUID.randomId());

    NameComponent name = new NameComponent("Harvester");
    entity.addComponent(name);

    ResourceHarvesterComponent resourceHarvesterComponent = new ResourceHarvesterComponent();
    resourceHarvesterComponent.setUnitsPerMinute(1);
    resourceHarvesterComponent.setResourceId(resourceService.getResource("Wood").get().getResourceId());
    entity.addComponent(resourceHarvesterComponent);

    ResourceStorageComponent resourceStorageComponent = new ResourceStorageComponent(0, 30);
    entity.addComponent(resourceStorageComponent);

    gameEngine.addEntity(entity);

    assertEquals(HarvestingProgress.HarvestingState.WAITING, resourceHarvesterComponent.getHarvestingProgress().getHarvestingState());
    assertEquals(0f, resourceHarvesterComponent.getHarvestingProgress().getHarvestingProgressPercent(), 0f);
    assertEquals(0, resourceStorageComponent.getResources().size());
    assertEquals(0, webSocketMessagingServiceTest.getBroadcastMessages(StartHarvestingMessage.class).size());

    //pass time by 30 seconds
    gameEngine.update(30f);

    assertEquals(HarvestingProgress.HarvestingState.WAITING, resourceHarvesterComponent.getHarvestingProgress().getHarvestingState());
    assertEquals(0f, resourceHarvesterComponent.getHarvestingProgress().getHarvestingProgressPercent(), 0f);
    assertEquals(0, resourceStorageComponent.getResources().size());
    assertEquals(0, webSocketMessagingServiceTest.getBroadcastMessages(StartHarvestingMessage.class).size());

    gameEngine.update(30f);

    assertEquals(HarvestingProgress.HarvestingState.WAITING, resourceHarvesterComponent.getHarvestingProgress().getHarvestingState());
    assertEquals(0f, resourceHarvesterComponent.getHarvestingProgress().getHarvestingProgressPercent(), 0f);
    assertEquals(0, resourceStorageComponent.getResources().size());
    assertEquals(0, webSocketMessagingServiceTest.getBroadcastMessages(StartHarvestingMessage.class).size());
  }

  @Test
  public void testHarvestTillCapacity() {
    Entity entity = gameEngine.createEmptyEntity(EntityUUID.randomId());

    NameComponent name = new NameComponent("Harvester");
    entity.addComponent(name);

    ResourceHarvesterComponent resourceHarvesterComponent = new ResourceHarvesterComponent();
    resourceHarvesterComponent.setUnitsPerMinute(1);
    resourceHarvesterComponent.setResourceId(resourceService.getResource("Wood").get().getResourceId());
    entity.addComponent(resourceHarvesterComponent);

    ResourceStorageComponent resourceStorageComponent = new ResourceStorageComponent(20, 30);
    entity.addComponent(resourceStorageComponent);

    gameEngine.addEntity(entity);

    assertEquals(HarvestingProgress.HarvestingState.WAITING, resourceHarvesterComponent.getHarvestingProgress().getHarvestingState());
    assertEquals(0f, resourceHarvesterComponent.getHarvestingProgress().getHarvestingProgressPercent(), 0f);
    assertEquals(0, resourceStorageComponent.getResources().size());

    //update 40 times one minute
    for(int i = 0; i < 20; i++) {
      gameEngine.update(60f);
    }
    assertEquals(20, webSocketMessagingServiceTest.getBroadcastMessages(StartHarvestingMessage.class).size());

    assertEquals(HarvestingProgress.HarvestingState.WAITING, resourceHarvesterComponent.getHarvestingProgress().getHarvestingState());
    assertEquals(0f, resourceHarvesterComponent.getHarvestingProgress().getHarvestingProgressPercent(), 0f);
    assertEquals(20, resourceStorageComponent.getResources().size());
  }

}