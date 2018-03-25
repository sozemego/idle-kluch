package com.soze.idlekluch.world.service;

import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.entity.TileId;
import com.soze.idlekluch.world.repository.WorldRepository;
import com.soze.klecs.engine.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityExistsException;
import java.awt.*;
import java.util.*;
import java.util.List;

@Service
public class World {

  private static final Logger LOG = LoggerFactory.getLogger(World.class);

  //TODO externalize
  private final int worldWidth = 25;
  private final int worldHeight = 25;

  private final WorldRepository worldRepository;
  private final Engine engine;
  private final Map<Point, Tile> allTiles;

  @Autowired
  public World(final WorldRepository worldRepository) {
    this.worldRepository = Objects.requireNonNull(worldRepository);
    this.engine = new Engine();
    this.allTiles = new HashMap<>();
  }

  @PostConstruct
  public void initWorld() {
    LOG.info("Initializing world");

    initResources();

    final List<Tile> allTiles = worldRepository.getAllTiles();
    LOG.info("Retrieved [{}] tiles.", allTiles.size());

    if(allTiles.isEmpty()) {
      LOG.info("There are no tiles, creating.");
      final List<Tile> newTiles = createInitialTiles();
      worldRepository.addTiles(newTiles);
      allTiles.addAll(newTiles);
      LOG.info("Added [{}] tiles", newTiles.size());
    }

    for(final Tile tile: allTiles) {
      this.allTiles.put(new Point(tile.getX(), tile.getY()), tile);
    }

  }

  /**
   * This method inputs hardcoded resources into the database
   * only if they do not exist. Later this will be replaced with
   * resources from a config file or just SQL script.
   */
  private void initResources() {
    LOG.info("Initializing world resources");

    final List<Resource> toAdd = new ArrayList<>();
    toAdd.add(new Resource(EntityUUID.randomId(), "Wood"));
    toAdd.add(new Resource(EntityUUID.randomId(), "Stone"));
    toAdd.add(new Resource(EntityUUID.randomId(), "Plank"));
    toAdd.add(new Resource(EntityUUID.randomId(), "Brick"));

    for(final Resource resourceToAdd: toAdd) {
      try {
        worldRepository.addResource(resourceToAdd);
        LOG.info("Resource [{}] added", resourceToAdd);
      } catch (EntityExistsException e) {

      }
    }

  }

  public Map<Point, Tile> getAllTiles() {
    return allTiles;
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
