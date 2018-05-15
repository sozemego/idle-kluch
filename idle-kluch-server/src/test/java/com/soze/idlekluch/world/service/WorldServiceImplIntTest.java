package com.soze.idlekluch.world.service;

import com.soze.idlekluch.RootConfig;
import com.soze.idlekluch.utils.sql.DatabaseReset;
import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.entity.TileId;
import com.soze.idlekluch.world.repository.WorldRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
public class WorldServiceImplIntTest {

  @Autowired
  private WorldService worldService;

  @Autowired
  private WorldRepository worldRepository;

  @BeforeClass
  public static void beforeClass() {
    DatabaseReset.resetDatabase();
  }

  @Before
  public void setup() {
    final List<Tile> allTiles = new ArrayList<>(worldRepository.getAllTiles().values());
    worldRepository.removeTiles(allTiles);
  }

  @Test
  public void testCreateChunkEmptyWorld() {
    final int centerX = 0;
    final int centerY = 0;
    final TileId chunkCenter = new TileId(centerX, centerY);
    final List<Tile> expectedTiles = new ArrayList<>();
    for(int i = -7; i < 8; i++) {
      for(int j = -7; j < 8; j++) {
        expectedTiles.add(new Tile(new TileId(chunkCenter.getX() + i, chunkCenter.getY() + j)));
      }
    }

    final List<Tile> newTiles = worldService.createWorldChunk(chunkCenter);
    assertEquals(new HashSet<>(expectedTiles), new HashSet<>(newTiles));

    final Map<TileId, Tile> allTiles = worldRepository.getAllTiles();
    for(final Tile tile: expectedTiles) {
      assertTrue(allTiles.containsKey(tile.getTileId()));
    }
    assertEquals(allTiles.size(), newTiles.size());
  }

  @Test
  public void testCreateChunkOnTheSameSpotShouldReturnEmptyList() {
    final int centerX = 0;
    final int centerY = 0;
    final TileId chunkCenter = new TileId(centerX, centerY);
    final List<Tile> expectedTiles = new ArrayList<>();
    for(int i = -7; i < 8; i++) {
      for(int j = -7; j < 8; j++) {
        expectedTiles.add(new Tile(new TileId(chunkCenter.getX() + i, chunkCenter.getY() + j)));
      }
    }

    final List<Tile> newTiles = worldService.createWorldChunk(chunkCenter);
    assertEquals(new HashSet<>(expectedTiles), new HashSet<>(newTiles));
    final List<Tile> newTiles1 = worldService.createWorldChunk(chunkCenter);
    assertEquals(0, newTiles1.size());

    final Map<TileId, Tile> allTiles = worldRepository.getAllTiles();
    for(final Tile tile: expectedTiles) {
      assertTrue(allTiles.containsKey(tile.getTileId()));
    }
    assertEquals(allTiles.size(), newTiles.size());
  }

  @Test
  public void testCreateChunkVeryFarAwayFromAnotherChunk() {
    final List<Tile> firstTiles = worldService.createWorldChunk(new TileId(0, 0));

    final int centerX = 555;
    final int centerY = 2500;
    final TileId chunkCenter = new TileId(centerX, centerY);
    final List<Tile> expectedTiles = new ArrayList<>();
    for(int i = -7; i < 8; i++) {
      for(int j = -7; j < 8; j++) {
        expectedTiles.add(new Tile(new TileId(chunkCenter.getX() + i, chunkCenter.getY() + j)));
      }
    }

    final List<Tile> newTiles = worldService.createWorldChunk(chunkCenter);
    assertEquals(new HashSet<>(expectedTiles), new HashSet<>(newTiles));

    final Map<TileId, Tile> allTiles = worldRepository.getAllTiles();
    for(final Tile tile: expectedTiles) {
      assertTrue(allTiles.containsKey(tile.getTileId()));
    }

    assertEquals(allTiles.size(), newTiles.size() + firstTiles.size());
  }

  @Test
  public void testCreateChunkNextToExistingChunk() {
    final List<Tile> firstTiles = worldService.createWorldChunk(new TileId(0, 0));

    final int centerX = -1;
    final int centerY = 0;
    final TileId chunkCenter = new TileId(centerX, centerY);
    final List<Tile> expectedTiles = new ArrayList<>();
    for(int i = -7; i < 8; i++) {
      expectedTiles.add(new Tile(new TileId(-8, chunkCenter.getY() + i)));
    }

    final List<Tile> newTiles = worldService.createWorldChunk(chunkCenter);
    assertEquals(new HashSet<>(expectedTiles), new HashSet<>(newTiles));

    final Map<TileId, Tile> allTiles = worldRepository.getAllTiles();
    for(final Tile tile: expectedTiles) {
      assertTrue(allTiles.containsKey(tile.getTileId()));
    }
    assertEquals(allTiles.size(), newTiles.size() + firstTiles.size());
  }

  @Test
  public void testCreateOneTileChunk() {
    final List<Tile> tiles = worldService.createWorldChunk(new TileId(0, 0), 1, 1);
    assertEquals(1, tiles.size());
  }

  @Test
  public void testCreateOneTileChunkSameSpot() {
    worldService.createWorldChunk(new TileId(0, 0), 1, 1);
    final List<Tile> tiles = worldService.createWorldChunk(new TileId(0, 0), 1, 1);
    assertEquals(0, tiles.size());
  }

  @Test
  public void testCreateLongRowChunk() {
    final List<Tile> tiles = worldService.createWorldChunk(new TileId(0, 0), 25, 1);
    assertEquals(25, tiles.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateChunkInvalidWidth() {
    worldService.createWorldChunk(new TileId(0, 0), -25, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateChunkInvalidHeight() {
    worldService.createWorldChunk(new TileId(0, 0), 25, 0);
  }

}