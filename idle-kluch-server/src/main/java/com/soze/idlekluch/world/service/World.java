package com.soze.idlekluch.world.service;

import com.soze.idlekluch.utils.CommonUtils;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.entity.TileId;
import com.soze.idlekluch.world.entity.Tree;
import com.soze.idlekluch.world.repository.WorldRepository;
import com.soze.klecs.engine.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * World is the in-memory representation of the game world.
 */
@Service
public class World {

  private static final Logger LOG = LoggerFactory.getLogger(World.class);

  //TODO externalize
  private final int worldWidth = 25;
  private final int worldHeight = 25;
  private final int tileSize = 128;

  private final WorldRepository worldRepository;
  private final Map<Point, Tile> allTiles;

  @Autowired
  public World(final WorldRepository worldRepository) {
    this.worldRepository = Objects.requireNonNull(worldRepository);
    this.allTiles = new HashMap<>();
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

    for(final Tile tile: allTiles) {
      this.allTiles.put(new Point(tile.getX(), tile.getY()), tile);
    }

    final List<Tree> allTrees = worldRepository.getAllTrees();
    LOG.info("Retrieved [{}] trees", allTrees.size());

    if(allTrees.isEmpty()) {
      LOG.info("There are no trees, generating!");
      final int trees = 125;
      final int maxX = worldWidth * tileSize;
      final int maxY = worldHeight * tileSize;

      for(int i = 0; i < trees; i++) {
        final int x = CommonUtils.randomNumber(0, maxX);
        final int y = CommonUtils.randomNumber(0, maxY);

        final Tree tree = new Tree();
        tree.setTreeId(EntityUUID.randomId());
        tree.setX(x);
        tree.setY(y);
        tree.setYield(25);
        tree.setDefinitionId("tree_" + CommonUtils.randomNumber(1, 2));

        worldRepository.addTree(tree);
      }

    }

  }

  public Map<Point, Tile> getAllTiles() {
    return allTiles;
  }

  public List<Tree> getAllTrees() {
    return worldRepository.getAllTrees();
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
