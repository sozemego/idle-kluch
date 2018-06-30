package com.soze.idlekluch.kingdom.service;

import com.soze.idlekluch.IntAuthTest;
import com.soze.idlekluch.RootConfig;
import com.soze.idlekluch.game.engine.EntityConverter;
import com.soze.idlekluch.game.engine.components.PhysicsComponent;
import com.soze.idlekluch.game.message.BuildBuildingForm;
import com.soze.idlekluch.game.service.EntityService;
import com.soze.idlekluch.game.service.GameEngine;
import com.soze.idlekluch.kingdom.exception.NoResourceInRadiusException;
import com.soze.idlekluch.core.utils.CommonUtils;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.core.utils.sql.DatabaseReset;
import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.entity.TileId;
import com.soze.idlekluch.world.repository.WorldRepository;
import com.soze.idlekluch.world.service.ResourceService;
import com.soze.idlekluch.world.service.WorldService;
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

import java.util.ArrayList;
import java.util.List;

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

  private static final String WOODCUTTER_ID = "7e10d339-dc10-4204-914c-cdfb2039460d";

  @Autowired
  private BuildingService buildingService;

  @Autowired
  private GameEngine gameEngine;

  @Autowired
  private KingdomService kingdomService;

  @Autowired
  private WorldService worldService;

  @Autowired
  private EntityService entityService;

  @Autowired
  private ResourceService resourceService;

  @Autowired
  private WorldRepository worldRepository;

  @Autowired
  private EntityConverter entityConverter;

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
    final Entity forest = gameEngine.createEmptyEntity();
    entityService.copyEntity(forestTemplate, forest);

    final PhysicsComponent physicsComponent = forest.getComponent(PhysicsComponent.class);
    physicsComponent.setX(2000);
    physicsComponent.setY(2000);
    gameEngine.addEntity(forest);

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
    final Entity forest = gameEngine.createEmptyEntity();
    entityService.copyEntity(forestTemplate, forest);

    final PhysicsComponent physicsComponent = forest.getComponent(PhysicsComponent.class);
    physicsComponent.setX(170);
    physicsComponent.setY(170);
    gameEngine.addEntity(forest);

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

}
