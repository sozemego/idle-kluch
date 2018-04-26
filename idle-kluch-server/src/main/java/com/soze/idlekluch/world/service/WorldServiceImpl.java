package com.soze.idlekluch.world.service;

import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.entity.TileId;
import com.soze.idlekluch.world.entity.World;
import com.soze.idlekluch.world.events.InitializeWorldEvent;
import com.soze.idlekluch.world.repository.WorldRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * World is the in-memory representation of the game world.
 * This means tiles, and some world data, NOT entities.
 */
@Service
public class WorldServiceImpl implements WorldService {

  private static final Logger LOG = LoggerFactory.getLogger(WorldServiceImpl.class);

  //TODO externalize
  private final int worldWidth = 25;
  private final int worldHeight = 25;
  private final int tileSize = 128;

  private final WorldRepository worldRepository;
  private final ApplicationEventPublisher publisher;
  private final Map<Point, Tile> allTiles;

  @Autowired
  public WorldServiceImpl(final WorldRepository worldRepository,
                          final ApplicationEventPublisher publisher) {
    this.worldRepository = Objects.requireNonNull(worldRepository);
    this.publisher = Objects.requireNonNull(publisher);
    this.allTiles = new HashMap<>();
  }

  @PostConstruct
  public void initWorld() {
    final Optional<World> worldOptional = worldRepository.getCurrentWorld();
    if(!worldOptional.isPresent()) {
      LOG.info("World is not initialized, starting.");
      worldRepository.saveWorld(new World());
      initTiles();

      //TODO instead of this, listen to spring on init event and then call initWorld
      final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
      scheduledExecutorService.schedule(
        () -> publisher.publishEvent(new InitializeWorldEvent()),
        5, TimeUnit.SECONDS
      );

    }
  }

  @Override
  public Map<Point, Tile> getAllTiles() {
    return allTiles;
  }

  @Override
  public int getWorldWidth() {
    return worldWidth;
  }

  @Override
  public int getWorldHeight() {
    return worldHeight;
  }

  @Override
  public int getTileSize() {
    return tileSize;
  }

  private void initTiles() {
    final List<Tile> allTiles = worldRepository.getAllTiles();
    LOG.info("Retrieved [{}] tiles.", allTiles.size());
    if(allTiles.isEmpty()) {
      final List<Tile> newTiles = createInitialTiles();
      allTiles.addAll(newTiles);
      worldRepository.addTiles(newTiles);
      LOG.info("Added [{}] tiles", newTiles.size());
    }

    allTiles.forEach(tile -> this.allTiles.put(new Point(tile.getX(), tile.getY()), tile));
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
