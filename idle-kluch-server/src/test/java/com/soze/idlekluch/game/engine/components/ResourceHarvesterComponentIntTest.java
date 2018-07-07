package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.RootConfig;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.core.utils.sql.DatabaseReset;
import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceHarvesterComponent;
import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceSourceSlot;
import com.soze.idlekluch.game.entity.PersistentEntity;
import com.soze.idlekluch.game.service.EntityService;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.world.service.ResourceService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(
  classes = {
    RootConfig.class
  }
)
@WebAppConfiguration
@ActiveProfiles({"integration-test"})
@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
public class ResourceHarvesterComponentIntTest {

  @Autowired
  private EntityService entityService;

  @Autowired
  private ResourceService resourceService;

  @Autowired
  private GameEngine gameEngine;

  @BeforeClass
  public static void setupClass() {
    DatabaseReset.deleteData();
  }

  @Before
  public void setup() {
    DatabaseReset.deleteData();
    gameEngine.reset();
  }

  @Test
  public void testOrderIsOkAfterPersistence() {
    final EntityUUID sourceId = EntityUUID.randomId();
    final PersistentEntity sourceEntity = new PersistentEntity();
    sourceEntity.setEntityId(sourceId);
    entityService.addEntity(sourceEntity);

    final EntityUUID id = EntityUUID.randomId();
    final PersistentEntity pe = new PersistentEntity();
    pe.setEntityId(id);
    final ResourceHarvesterComponent resourceHarvesterComponent = new ResourceHarvesterComponent();
    resourceHarvesterComponent.setSourceSlots(3);
    resourceHarvesterComponent.setSource(sourceId, 2);
    resourceHarvesterComponent.setResourceId(resourceService.getResource("Wood").get().getResourceId());
    pe.setResourceHarvesterComponent(resourceHarvesterComponent);
    entityService.addEntity(pe);

    assertEquals(3, resourceHarvesterComponent.getSources().size());
    assertEquals(null, resourceHarvesterComponent.getSources().get(0).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSources().get(1).getSourceId());
    assertEquals(sourceId, resourceHarvesterComponent.getSources().get(2).getSourceId());

    final PersistentEntity retrievedPe = entityService.getEntity(id).get();
    final ResourceHarvesterComponent retrievedHarvesterComponent = retrievedPe.getResourceHarvesterComponent();
    assertEquals(3, retrievedHarvesterComponent.getSources().size());
    assertEquals(null, retrievedHarvesterComponent.getSources().get(0).getSourceId());
    assertEquals(null, retrievedHarvesterComponent.getSources().get(1).getSourceId());
    assertEquals(sourceId, retrievedHarvesterComponent.getSources().get(2).getSourceId());
  }

  @Test
  public void testOrderIsOkAfterPersistence2() {
    final EntityUUID sourceId = EntityUUID.randomId();
    final PersistentEntity sourceEntity = new PersistentEntity();
    sourceEntity.setEntityId(sourceId);
    entityService.addEntity(sourceEntity);

    final EntityUUID id = EntityUUID.randomId();
    final PersistentEntity pe = new PersistentEntity();
    pe.setEntityId(id);
    final ResourceHarvesterComponent resourceHarvesterComponent = new ResourceHarvesterComponent();
    resourceHarvesterComponent.setSourceSlots(25);
    resourceHarvesterComponent.setSource(sourceId, 12);
    final List<ResourceSourceSlot> slots = resourceHarvesterComponent.getSources();
    Collections.shuffle(slots);
    resourceHarvesterComponent.setSources(slots);
    resourceHarvesterComponent.setResourceId(resourceService.getResource("Wood").get().getResourceId());
    pe.setResourceHarvesterComponent(resourceHarvesterComponent);
    entityService.addEntity(pe);

    final PersistentEntity retrievedPe = entityService.getEntity(id).get();
    final ResourceHarvesterComponent retrievedHarvesterComponent = retrievedPe.getResourceHarvesterComponent();
    assertEquals(25, retrievedHarvesterComponent.getSources().size());
    assertEquals(null, retrievedHarvesterComponent.getSources().get(0).getSourceId());
    assertEquals(null, retrievedHarvesterComponent.getSources().get(1).getSourceId());
    assertEquals(sourceId, retrievedHarvesterComponent.getSources().get(12).getSourceId());
  }
}
