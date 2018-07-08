package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceHarvesterComponent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ResourceHarvesterComponentTest {

  @Test
  public void testNoResourcesListFilledWithNullSourceIds() {
    final ResourceHarvesterComponent resourceHarvesterComponent = new ResourceHarvesterComponent();
    resourceHarvesterComponent.setSourceSlots(1);
    assertEquals(1, resourceHarvesterComponent.getSlots().size());
    assertEquals(null, resourceHarvesterComponent.getSlots().get(0).getSourceId());
  }

  @Test
  public void testNoResourcesListFilledWithNullSourceIdsManySlots() {
    final ResourceHarvesterComponent resourceHarvesterComponent = new ResourceHarvesterComponent();
    resourceHarvesterComponent.setSourceSlots(5);
    assertEquals(5, resourceHarvesterComponent.getSlots().size());
    assertEquals(null, resourceHarvesterComponent.getSlots().get(0).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSlots().get(1).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSlots().get(2).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSlots().get(3).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSlots().get(4).getSourceId());
  }

  @Test
  public void testNoResourcesSetOneResource() {
    final ResourceHarvesterComponent resourceHarvesterComponent = new ResourceHarvesterComponent();
    resourceHarvesterComponent.setSourceSlots(5);
    final EntityUUID id = EntityUUID.randomId();
    resourceHarvesterComponent.setSlot(id, 3);
    assertEquals(5, resourceHarvesterComponent.getSlots().size());
    assertEquals(null, resourceHarvesterComponent.getSlots().get(0).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSlots().get(1).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSlots().get(2).getSourceId());
    assertEquals(id, resourceHarvesterComponent.getSlots().get(3).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSlots().get(4).getSourceId());
  }

  @Test
  public void testUnsetResource() {
    final ResourceHarvesterComponent resourceHarvesterComponent = new ResourceHarvesterComponent();
    resourceHarvesterComponent.setSourceSlots(5);
    final EntityUUID id = EntityUUID.randomId();
    resourceHarvesterComponent.setSlot(id, 3);
    System.out.println(resourceHarvesterComponent.getSlots());
    assertEquals(5, resourceHarvesterComponent.getSlots().size());
    assertEquals(null, resourceHarvesterComponent.getSlots().get(0).getSourceId());
    assertEquals(0, resourceHarvesterComponent.getSlots().get(0).getSlotNumber());
    assertEquals(null, resourceHarvesterComponent.getSlots().get(1).getSourceId());
    assertEquals(1, resourceHarvesterComponent.getSlots().get(1).getSlotNumber());
    assertEquals(null, resourceHarvesterComponent.getSlots().get(2).getSourceId());
    assertEquals(2, resourceHarvesterComponent.getSlots().get(2).getSlotNumber());
    assertEquals(id, resourceHarvesterComponent.getSlots().get(3).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSlots().get(4).getSourceId());
    assertEquals(4, resourceHarvesterComponent.getSlots().get(4).getSlotNumber());

    resourceHarvesterComponent.setSlot(null, 3);
    assertEquals(5, resourceHarvesterComponent.getSlots().size());
    assertEquals(null, resourceHarvesterComponent.getSlots().get(0).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSlots().get(1).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSlots().get(2).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSlots().get(3).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSlots().get(4).getSourceId());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetSourceInvalidIndex() {
    final ResourceHarvesterComponent resourceHarvesterComponent = new ResourceHarvesterComponent();
    resourceHarvesterComponent.setSourceSlots(4);
    resourceHarvesterComponent.setSlot(EntityUUID.randomId(), 5);
  }

}