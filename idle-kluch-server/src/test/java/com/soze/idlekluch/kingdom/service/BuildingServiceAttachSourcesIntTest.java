package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.IntAuthTest;
import com.soze.idlekluch.RootConfig;
import com.soze.idlekluch.core.exception.EntityDoesNotExistException;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.core.utils.sql.DatabaseReset;
import com.soze.idlekluch.game.engine.components.OwnershipComponent;
import com.soze.idlekluch.game.engine.components.PhysicsComponent;
import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceHarvesterComponent;
import com.soze.idlekluch.game.exception.*;
import com.soze.idlekluch.game.message.AttachResourceSourceForm;
import com.soze.idlekluch.game.service.EntityResourceService;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.kingdom.entity.Kingdom;
import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.idlekluch.world.service.ResourceService;
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
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(
  classes = {
    RootConfig.class
  }
)
@WebAppConfiguration
@ActiveProfiles({"integration-test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BuildingServiceAttachSourcesIntTest extends IntAuthTest {

  @Autowired
  private BuildingService buildingService;

  @Autowired
  private GameEngine gameEngine;

  @Autowired
  private EntityResourceService entityResourceService;

  @Autowired
  private ResourceService resourceService;

  @BeforeClass
  public static void beforeClass() {
    DatabaseReset.deleteData();
  }

  @Before
  public void setup() {
    DatabaseReset.deleteData();
    gameEngine.reset();
  }

  @Test(expected = EntityDoesNotHaveComponentException.class)
  public void testAttachResourceSourceNotAHarvester() {
    final EntityUUID buildingId = EntityUUID.randomId();
    gameEngine.addEntity(gameEngine.createEntityWithName(buildingId, "Name"));
    final EntityUUID sourceId = EntityUUID.randomId();
    buildingService.attachResourceSource("", getForm(buildingId, sourceId, 1));
  }

  @Test(expected = EntityDoesNotExistException.class)
  public void testAttachResourceSourceHarvesterDoesNotExist() {
    buildingService.attachResourceSource("", getForm(EntityUUID.randomId(), EntityUUID.randomId(), 1));
  }

  @Test(expected = EntityDoesNotExistException.class)
  public void testAttachResourceSourceSourceDoesNotExist() {
    final EntityUUID buildingId = EntityUUID.randomId();
    final Entity building = gameEngine.createEntityWithName(buildingId, "Name");
    building.addComponent(new ResourceHarvesterComponent(EntityUUID.randomId(), 12, 5, 1, new ArrayList<>(), 1));
    gameEngine.addEntity(building);
    buildingService.attachResourceSource("", getForm(buildingId, EntityUUID.randomId(), 1));
  }

  @Test(expected = InvalidResourceException.class)
  public void testAttachResourceSourceWrongResource() {
    final EntityUUID buildingId = EntityUUID.randomId();
    final Entity building = gameEngine.createEntityWithName(buildingId, "Name");
    building.addComponent(new ResourceHarvesterComponent(EntityUUID.randomId(), 12, 5, 1, new ArrayList<>(), 1));
    gameEngine.addEntity(building);

    final Resource resource = resourceService.getResource("Wood").get();
    final List<Entity> resourceSourceTemplates = entityResourceService.getResourceEntityTemplates(resource);
    final Entity placedResource = entityResourceService.placeResourceSource((EntityUUID) resourceSourceTemplates.get(0).getId(), new Point(0, 0));

    buildingService.attachResourceSource("", getForm(buildingId, (EntityUUID) placedResource.getId(), 1));
  }

  @Test(expected = NoResourceInRadiusException.class)
  public void testAttachSourceIsTooFarAway() {
    final EntityUUID buildingId = EntityUUID.randomId();
    final Entity building = gameEngine.createEntityWithName(buildingId, "Name");
    final Resource resource = resourceService.getResource("Wood").get();
    building.addComponent(new ResourceHarvesterComponent(resource.getResourceId(), 12, 5, 1, new ArrayList<>(), 1));
    building.addComponent(new PhysicsComponent(0, 0, 50, 50));
    gameEngine.addEntity(building);

    final List<Entity> resourceSourceTemplates = entityResourceService.getResourceEntityTemplates(resource);
    final Entity placedResource = entityResourceService.placeResourceSource((EntityUUID) resourceSourceTemplates.get(0).getId(), new Point(1000, 1000));

    buildingService.attachResourceSource("", getForm(buildingId, (EntityUUID) placedResource.getId(), 1));
  }

  @Test(expected = InvalidResourceSlotException.class)
  public void testAttachResourceInvalidSlot() {
    final EntityUUID buildingId = EntityUUID.randomId();
    final Entity building = gameEngine.createEntityWithName(buildingId, "Name");
    final Resource resource = resourceService.getResource("Wood").get();
    building.addComponent(new ResourceHarvesterComponent(resource.getResourceId(), 5000, 5, 1, new ArrayList<>(), 1));
    building.addComponent(new PhysicsComponent(0, 0, 50, 50));
    gameEngine.addEntity(building);

    final List<Entity> resourceSourceTemplates = entityResourceService.getResourceEntityTemplates(resource);
    final Entity placedResource = entityResourceService.placeResourceSource((EntityUUID) resourceSourceTemplates.get(0).getId(), new Point(150, 150));

    buildingService.attachResourceSource("", getForm(buildingId, (EntityUUID) placedResource.getId(), 5));
  }

  @Test
  public void testAttachResourceValid() {
    final EntityUUID buildingId = EntityUUID.randomId();
    final Entity building = gameEngine.createEntityWithName(buildingId, "Name");

    final Resource resource = resourceService.getResource("Wood").get();
    building.addComponent(new ResourceHarvesterComponent(resource.getResourceId(), 5000, 5, 1, new ArrayList<>(), 1));
    building.addComponent(new PhysicsComponent(0, 0, 50, 50));
    gameEngine.addEntity(building);

    final ResourceHarvesterComponent harvester = building.getComponent(ResourceHarvesterComponent.class);
    assertEquals(null, harvester.getSlots().get(0).getSourceId());

    final List<Entity> resourceSourceTemplates = entityResourceService.getResourceEntityTemplates(resource);
    final Entity placedResource = entityResourceService.placeResourceSource((EntityUUID) resourceSourceTemplates.get(0).getId(), new Point(150, 150));

    buildingService.attachResourceSource("username", getForm(buildingId, (EntityUUID) placedResource.getId(), 1));
    assertNotEquals(null, harvester.getSlots().get(0).getSourceId());
    assertEquals(placedResource.getId(), harvester.getSlots().get(0).getSourceId());
  }

  @Test(expected = InvalidOwnerException.class)
  public void testDifferentUserIsOwner() {
    final Kingdom kingdom = createKingdom("user", "kingdom");
    final EntityUUID buildingId = EntityUUID.randomId();
    final Entity building = gameEngine.createEntityWithName(buildingId, "Name");
    final Resource resource = resourceService.getResource("Wood").get();
    building.addComponent(new ResourceHarvesterComponent(resource.getResourceId(), 5000, 5, 1, new ArrayList<>(), 1));
    building.addComponent(new OwnershipComponent(kingdom.getKingdomId()));
    gameEngine.addEntity(building);

    buildingService.attachResourceSource("Username", getForm(buildingId, EntityUUID.randomId(), 1));
  }

  private AttachResourceSourceForm getForm(final EntityUUID harvesterId, final EntityUUID sourceId, final int slot) {
    return new AttachResourceSourceForm(UUID.randomUUID(), harvesterId, sourceId, slot);
  }

}
