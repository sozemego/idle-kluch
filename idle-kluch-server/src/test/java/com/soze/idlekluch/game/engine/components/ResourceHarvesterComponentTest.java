package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceHarvesterComponent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class ResourceHarvesterComponentTest {

  @Test
  public void testNoResourcesListFilledWithNullSourceIds() {
    final ResourceHarvesterComponent resourceHarvesterComponent = new ResourceHarvesterComponent();
    resourceHarvesterComponent.setSourceSlots(1);
    assertEquals(1, resourceHarvesterComponent.getSources().size());
    assertEquals(null, resourceHarvesterComponent.getSources().get(0).getSourceId());
  }

  @Test
  public void testNoResourcesListFilledWithNullSourceIdsManySlots() {
    final ResourceHarvesterComponent resourceHarvesterComponent = new ResourceHarvesterComponent();
    resourceHarvesterComponent.setSourceSlots(5);
    assertEquals(5, resourceHarvesterComponent.getSources().size());
    assertEquals(null, resourceHarvesterComponent.getSources().get(0).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSources().get(1).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSources().get(2).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSources().get(3).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSources().get(4).getSourceId());
  }

  @Test
  public void testNoResourcesSetOneResource() {
    final ResourceHarvesterComponent resourceHarvesterComponent = new ResourceHarvesterComponent();
    resourceHarvesterComponent.setSourceSlots(5);
    final EntityUUID id = EntityUUID.randomId();
    resourceHarvesterComponent.setSource(id, 3);
    assertEquals(5, resourceHarvesterComponent.getSources().size());
    assertEquals(null, resourceHarvesterComponent.getSources().get(0).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSources().get(1).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSources().get(2).getSourceId());
    assertEquals(id, resourceHarvesterComponent.getSources().get(3).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSources().get(4).getSourceId());
  }

  @Test
  public void testUnsetResource() {
    final ResourceHarvesterComponent resourceHarvesterComponent = new ResourceHarvesterComponent();
    resourceHarvesterComponent.setSourceSlots(5);
    final EntityUUID id = EntityUUID.randomId();
    resourceHarvesterComponent.setSource(id, 3);
    System.out.println(resourceHarvesterComponent.getSources());
    assertEquals(5, resourceHarvesterComponent.getSources().size());
    assertEquals(null, resourceHarvesterComponent.getSources().get(0).getSourceId());
    assertEquals(0, resourceHarvesterComponent.getSources().get(0).getSlotNumber());
    assertEquals(null, resourceHarvesterComponent.getSources().get(1).getSourceId());
    assertEquals(1, resourceHarvesterComponent.getSources().get(1).getSlotNumber());
    assertEquals(null, resourceHarvesterComponent.getSources().get(2).getSourceId());
    assertEquals(2, resourceHarvesterComponent.getSources().get(2).getSlotNumber());
    assertEquals(id, resourceHarvesterComponent.getSources().get(3).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSources().get(4).getSourceId());
    assertEquals(4, resourceHarvesterComponent.getSources().get(4).getSlotNumber());

    resourceHarvesterComponent.setSource(null, 3);
    assertEquals(5, resourceHarvesterComponent.getSources().size());
    assertEquals(null, resourceHarvesterComponent.getSources().get(0).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSources().get(1).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSources().get(2).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSources().get(3).getSourceId());
    assertEquals(null, resourceHarvesterComponent.getSources().get(4).getSourceId());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetSourceInvalidIndex() {
    final ResourceHarvesterComponent resourceHarvesterComponent = new ResourceHarvesterComponent();
    resourceHarvesterComponent.setSourceSlots(4);
    resourceHarvesterComponent.setSource(EntityUUID.randomId(), 5);
  }

}