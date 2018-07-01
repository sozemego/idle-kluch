package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class ResourceHarvesterComponentTest {

  @Test
  public void testNoResourcesListFilledWithNulls() {
    final ResourceHarvesterComponent resourceHarvesterComponent = new ResourceHarvesterComponent();
    resourceHarvesterComponent.setSourceSlots(1);
    assertEquals(1, resourceHarvesterComponent.getSources().size());
    assertEquals(null, new ArrayList<>(resourceHarvesterComponent.getSources()).get(0));
  }

  @Test
  public void testNoResourcesListFilledWithNullsManySlots() {
    final ResourceHarvesterComponent resourceHarvesterComponent = new ResourceHarvesterComponent();
    resourceHarvesterComponent.setSourceSlots(5);
    assertEquals(5, resourceHarvesterComponent.getSources().size());
    assertEquals(null, new ArrayList<>(resourceHarvesterComponent.getSources()).get(0));
    assertEquals(null, new ArrayList<>(resourceHarvesterComponent.getSources()).get(1));
    assertEquals(null, new ArrayList<>(resourceHarvesterComponent.getSources()).get(2));
    assertEquals(null, new ArrayList<>(resourceHarvesterComponent.getSources()).get(3));
    assertEquals(null, new ArrayList<>(resourceHarvesterComponent.getSources()).get(4));
  }

  @Test
  public void testNoResourcesSetOneResource() {
    final ResourceHarvesterComponent resourceHarvesterComponent = new ResourceHarvesterComponent();
    resourceHarvesterComponent.setSourceSlots(5);
    final EntityUUID id = EntityUUID.randomId();
    resourceHarvesterComponent.setSource(id, 3);
    assertEquals(5, resourceHarvesterComponent.getSources().size());
    assertEquals(null, new ArrayList<>(resourceHarvesterComponent.getSources()).get(0));
    assertEquals(null, new ArrayList<>(resourceHarvesterComponent.getSources()).get(1));
    assertEquals(null, new ArrayList<>(resourceHarvesterComponent.getSources()).get(2));
    assertEquals(id, new ArrayList<>(resourceHarvesterComponent.getSources()).get(3));
    assertEquals(null, new ArrayList<>(resourceHarvesterComponent.getSources()).get(4));
  }

  @Test
  public void testUnsetResource() {
    final ResourceHarvesterComponent resourceHarvesterComponent = new ResourceHarvesterComponent();
    resourceHarvesterComponent.setSourceSlots(5);
    final EntityUUID id = EntityUUID.randomId();
    resourceHarvesterComponent.setSource(id, 3);
    assertEquals(5, resourceHarvesterComponent.getSources().size());
    assertEquals(null, new ArrayList<>(resourceHarvesterComponent.getSources()).get(0));
    assertEquals(null, new ArrayList<>(resourceHarvesterComponent.getSources()).get(1));
    assertEquals(null, new ArrayList<>(resourceHarvesterComponent.getSources()).get(2));
    assertEquals(id, new ArrayList<>(resourceHarvesterComponent.getSources()).get(3));
    assertEquals(null, new ArrayList<>(resourceHarvesterComponent.getSources()).get(4));

    resourceHarvesterComponent.setSource(null, 3);
    assertEquals(5, resourceHarvesterComponent.getSources().size());
    assertEquals(null, new ArrayList<>(resourceHarvesterComponent.getSources()).get(0));
    assertEquals(null, new ArrayList<>(resourceHarvesterComponent.getSources()).get(1));
    assertEquals(null, new ArrayList<>(resourceHarvesterComponent.getSources()).get(2));
    assertEquals(null, new ArrayList<>(resourceHarvesterComponent.getSources()).get(3));
    assertEquals(null, new ArrayList<>(resourceHarvesterComponent.getSources()).get(4));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetSourceInvalidIndex() {
    final ResourceHarvesterComponent resourceHarvesterComponent = new ResourceHarvesterComponent();
    resourceHarvesterComponent.setSourceSlots(4);
    resourceHarvesterComponent.setSource(EntityUUID.randomId(), 5);
  }

}