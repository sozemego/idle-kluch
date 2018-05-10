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

import static org.junit.Assert.assertEquals;

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
        final Tile tile = new Tile();
        tile.setTileId(new TileId(chunkCenter.getX() + i, chunkCenter.getY() + j));
        expectedTiles.add(tile);
      }
    }

    final List<Tile> newTiles = worldService.createWorldChunk(chunkCenter);
    assertEquals(new HashSet<>(expectedTiles), new HashSet<>(newTiles));
  }

  @Test
  public void testCreateChunkOnTheSameSpotShouldReturnEmptyList() {
    final int centerX = 0;
    final int centerY = 0;
    final TileId chunkCenter = new TileId(centerX, centerY);
    final List<Tile> expectedTiles = new ArrayList<>();
    for(int i = -7; i < 8; i++) {
      for(int j = -7; j < 8; j++) {
        final Tile tile = new Tile();
        tile.setTileId(new TileId(chunkCenter.getX() + i, chunkCenter.getY() + j));
        expectedTiles.add(tile);
      }
    }

    final List<Tile> newTiles = worldService.createWorldChunk(chunkCenter);
    assertEquals(new HashSet<>(expectedTiles), new HashSet<>(newTiles));
    final List<Tile> newTiles1 = worldService.createWorldChunk(chunkCenter);
    assertEquals(new HashSet<>(), new HashSet<>(newTiles1));
  }

  @Test
  public void testCreateChunkVeryFarAwayFromAnotherChunk() {
    worldService.createWorldChunk(new TileId(0, 0));

    final int centerX = 555;
    final int centerY = 2500;
    final TileId chunkCenter = new TileId(centerX, centerY);
    final List<Tile> expectedTiles = new ArrayList<>();
    for(int i = -7; i < 8; i++) {
      for(int j = -7; j < 8; j++) {
        final Tile tile = new Tile();
        tile.setTileId(new TileId(chunkCenter.getX() + i, chunkCenter.getY() + j));
        expectedTiles.add(tile);
      }
    }

    final List<Tile> newTiles = worldService.createWorldChunk(chunkCenter);
    assertEquals(new HashSet<>(expectedTiles), new HashSet<>(newTiles));
  }

  @Test
  public void testCreateChunkNextToExistingChunk() {
    worldService.createWorldChunk(new TileId(0, 0));

    final int centerX = -1;
    final int centerY = 0;
    final TileId chunkCenter = new TileId(centerX, centerY);
    final List<Tile> expectedTiles = new ArrayList<>();
    for(int i = -7; i < 8; i++) {
      final Tile tile = new Tile();
      tile.setTileId(new TileId(-8, chunkCenter.getY() + i));
      expectedTiles.add(tile);
    }

    final List<Tile> newTiles = worldService.createWorldChunk(chunkCenter);
    assertEquals(new HashSet<>(expectedTiles), new HashSet<>(newTiles));
  }

}