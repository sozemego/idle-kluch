package com.soze.idlekluch.world.service;

import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.entity.TileId;
import com.soze.idlekluch.world.repository.WorldRepository;
import com.soze.klecs.engine.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class World {

  private static final Logger LOG = LoggerFactory.getLogger(World.class);

  //TODO externalize
  private final int worldWidth = 100;
  private final int worldHeight = 100;

  private final WorldRepository worldRepository;
  private final Engine engine;
  private final List<Tile> allTiles;

  @Autowired
  public World(final WorldRepository worldRepository) {
    this.worldRepository = Objects.requireNonNull(worldRepository);
    this.engine = new Engine();
    this.allTiles = new ArrayList<>();
  }

  @PostConstruct
  public void initWorld() {
    LOG.info("Initializing world");

    final List<Tile> allTiles = worldRepository.getAllTiles();
    LOG.info("Retrieved [{}] tiles.", allTiles.size());

    if(allTiles.isEmpty()) {
      LOG.info("There are no tiles, creating.");
      final List<Tile> newTiles = createInitialTiles();
      worldRepository.addTiles(newTiles);
      allTiles.addAll(newTiles);
      LOG.info("Added [{}] tiles", newTiles.size());
    }

    this.allTiles.addAll(allTiles);

  }

  private List<Tile> createInitialTiles() {
    final List<Tile> newTiles = new ArrayList<>();
    for(int i = 0; i < worldWidth; i++) {
      for(int j = 0; j < worldHeight; j++) {
        final Tile tile = new Tile();
        tile.setTileId(TileId.from(i, j));
        newTiles.add(tile);
      }
    }
    return newTiles;
  }


}
