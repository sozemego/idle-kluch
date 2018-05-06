package com.soze.idlekluch.world.service;

import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.entity.TileId;
import com.soze.idlekluch.world.entity.World;
import com.soze.idlekluch.world.repository.WorldRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * World is the in-memory representation of the game world.
 * This means tiles, and some world data, NOT entities.
 */
@Service
public class WorldServiceImpl implements WorldService {

  private static final Logger LOG = LoggerFactory.getLogger(WorldServiceImpl.class);

  //TODO externalize
  private final int maxWorldWidth = 2500;
  private final int maxWorldHeight = 2500;
  private final int tileSize = 128;

  private final WorldRepository worldRepository;

  @Autowired
  public WorldServiceImpl(final WorldRepository worldRepository) {
    this.worldRepository = Objects.requireNonNull(worldRepository);
  }

  @PostConstruct
  public void setup() {
    LOG.info("Initializing world");
    final Optional<World> worldOptional = worldRepository.getCurrentWorld();
    if(!worldOptional.isPresent()) {
      LOG.info("World is not initialized, creating new one.");
      worldRepository.saveWorld(new World());
    }

  }

  @Override
  public Map<TileId, Tile> getAllTiles() {
    return worldRepository.getAllTiles();
  }

  @Override
  public int getMaxWorldWidth() {
    return maxWorldWidth;
  }

  @Override
  public int getMaxWorldHeight() {
    return maxWorldHeight;
  }

  @Override
  public int getTileSize() {
    return tileSize;
  }

  @Override
  public void createWorldChunk(final TileId startingPoint) {
    //TODO TEST THIS
    LOG.info("Creating chunk at a point [{}]", startingPoint);
    //1. a world chunk will be a 15x15 tile region
    //   containing trees/mountains/resources etc.

    //2. find tileIds which will be part of the chunk
    final List<TileId> chunkTiles = new ArrayList<>();
    for (int i = -7; i < 8; i++) {
      for (int j = -7; j < 8; j++) {
        chunkTiles.add(new TileId(startingPoint.getX() + i, startingPoint.getY() + j));
      }
    }
    LOG.info("Will generate chunk from [{}, {}] to [{}, {}]",
      startingPoint.getX() - 7, startingPoint.getY() - 7,
      startingPoint.getX() + 7, startingPoint.getY() + 7
    );

    //3. remove those tileIds which already exist
    LOG.info("Removing tiles from chunk which already exist.");
    chunkTiles.removeIf(tileId -> worldRepository.getTile(tileId).isPresent());

    //4. create Tile objects
    final List<Tile> tiles = chunkTiles
                               .stream()
                               .map(Tile::new)
                               .collect(Collectors.toList());

    worldRepository.addTiles(tiles);

    //5. nothing more for now. forests/mountains/etc soon to come
  }

}
