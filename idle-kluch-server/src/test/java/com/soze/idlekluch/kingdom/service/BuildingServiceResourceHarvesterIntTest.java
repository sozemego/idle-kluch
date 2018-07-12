package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.IntAuthTest;
import com.soze.idlekluch.RootConfig;
import com.soze.idlekluch.core.utils.CommonUtils;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.core.utils.sql.DatabaseReset;
import com.soze.idlekluch.game.engine.components.ResourceSourceComponent;
import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceHarvesterComponent;
import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.game.service.EntityResourceService;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.game.exception.NoResourceInRadiusException;
import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.entity.TileId;
import com.soze.idlekluch.world.repository.WorldRepository;
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

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(
  classes = {
    RootConfig.class
  }
)
@WebAppConfiguration
@ActiveProfiles({"integration-test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BuildingServiceResourceHarvesterIntTest extends IntAuthTest {

  private static final String WOODCUTTER_ID = "03c99070-66d5-4dea-b57a-39b4f308a505";

  @Autowired
  private BuildingService buildingService;

  @Autowired
  private GameEngine gameEngine;

  @Autowired
  private EntityResourceService resourceService;

  @Autowired
  private WorldRepository worldRepository;

  @BeforeClass
  public static void beforeClass() {
    DatabaseReset.deleteData();
  }

  @Before
  public void setup() {
    worldRepository.removeTiles(new ArrayList<>(worldRepository.getAllTiles().values()));
    DatabaseReset.deleteData();
    gameEngine.reset();
  }

  @Test(expected = NoResourceInRadiusException.class)
  public void testPlaceHarvestingBuildingNoResource() {
    final Tile buildingTile = new Tile(new TileId(0, 0));
    worldRepository.addTile(buildingTile);

    final String username = CommonUtils.generateRandomString(12);
    createKingdom(username, CommonUtils.generateRandomString(12));

    resourceService
      .getAllResourceSources()
      .forEach(source -> gameEngine.deleteEntity((EntityUUID) source.getId()));

    buildingService.buildBuilding(
      username,
      new BuildBuildingForm(
        EntityUUID.randomId().toString(),
        WOODCUTTER_ID,
        buildingTile.getX() - 25,
        buildingTile.getY() - 25
      ));
  }

  @Test(expected = NoResourceInRadiusException.class)
  public void testPlaceHarvestingBuildingNoResourceInRadius() {
    final Tile buildingTile = new Tile(new TileId(0, 0));
    worldRepository.addTile(buildingTile);

    final List<Entity> woodSources = resourceService.getResourceEntityTemplates("Wood");
    final Entity forestTemplate = woodSources.get(0);
    resourceService.placeResourceSource((EntityUUID) forestTemplate.getId(), new Point(2000, 2000));

    final String username = CommonUtils.generateRandomString(12);
    createKingdom(username, CommonUtils.generateRandomString(12));

    resourceService
      .getAllResourceSources()
      .forEach(source -> gameEngine.deleteEntity((EntityUUID) source.getId()));

    buildingService.buildBuilding(
      username,
      new BuildBuildingForm(
        EntityUUID.randomId().toString(),
        WOODCUTTER_ID,
        buildingTile.getX() - 25,
        buildingTile.getY() - 25
      ));
  }

  @Test
  public void testPlaceHarvestingBuildingResourceInRadius() {
    final Tile buildingTile = new Tile(new TileId(0, 0));
    worldRepository.addTile(buildingTile);

    final List<Entity> woodSources = resourceService.getResourceEntityTemplates("Wood");
    final Entity forestTemplate = woodSources.get(0);
    resourceService.placeResourceSource((EntityUUID) forestTemplate.getId(), new Point(170, 170));

    final String username = CommonUtils.generateRandomString(12);
    createKingdom(username, CommonUtils.generateRandomString(12));
    buildingService.buildBuilding(
      username,
      new BuildBuildingForm(
        EntityUUID.randomId().toString(),
        WOODCUTTER_ID,
        buildingTile.getX() - 25,
        buildingTile.getY() - 25
      ));
  }

  @Test
  public void testPlaceHarvesterShouldAttachToHighestBonusSource() {
    final Tile buildingTile = new Tile(new TileId(0, 0));
    worldRepository.addTile(buildingTile);

    final List<Entity> woodSources = resourceService.getResourceEntityTemplates("Wood");
    final Entity forestTemplate = woodSources.get(0);
    resourceService.placeResourceSource((EntityUUID) forestTemplate.getId(), new Point(170, 170));

    final Entity smallForestTemplate = woodSources.get(1);
    resourceService.placeResourceSource((EntityUUID) smallForestTemplate.getId(), new Point(-170, -170));

    final String username = CommonUtils.generateRandomString(12);
    createKingdom(username, CommonUtils.generateRandomString(12));
    final Entity building = buildingService.buildBuilding(
      username,
      new BuildBuildingForm(
        EntityUUID.randomId().toString(),
        WOODCUTTER_ID,
        buildingTile.getX() - 25,
        buildingTile.getY() - 25
      ));

    final ResourceHarvesterComponent resourceHarvesterComponent = building.getComponent(ResourceHarvesterComponent.class);
    final Entity chosenSource = gameEngine.getEntity(resourceHarvesterComponent.getSlots().get(0).getSourceId()).get();
    final ResourceSourceComponent source = chosenSource.getComponent(ResourceSourceComponent.class);
    assertEquals(1.2f, source.getBonus(), 0.01);
  }

}
