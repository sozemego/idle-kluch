package com.soze.idlekluch.world.service;

import com.soze.idlekluch.aop.annotations.Profiled;
import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.entity.TileId;
import com.soze.idlekluch.world.entity.World;
import com.soze.idlekluch.world.events.WorldChunkCreatedEvent;
import com.soze.idlekluch.world.repository.WorldRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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

  private final WorldRepository worldRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Autowired
  public WorldServiceImpl(final WorldRepository worldRepository,
                          final ApplicationEventPublisher eventPublisher) {
    this.worldRepository = Objects.requireNonNull(worldRepository);
    this.eventPublisher = Objects.requireNonNull(eventPublisher);
  }

  @PostConstruct
  public void setup() {
    if(!worldRepository.getCurrentWorld().isPresent()) {
      LOG.info("World is not initialized, creating new one.");
      worldRepository.saveWorld(new World());
    }

  }

  @Override
  public Map<TileId, Tile> getAllTiles() {
    return worldRepository.getAllTiles();
  }

  @Override
  @Profiled
  public List<Tile> createWorldChunk(final TileId center) {
    return createWorldChunk(center, 15, 15);
  }

  @Override
  @Profiled
  public synchronized List<Tile> createWorldChunk(final TileId center, final int width, final int height) {
    Objects.requireNonNull(center);
    if(width < 1) {
      throw new IllegalArgumentException("Chunk width cannot be less than 1");
    }
    if(height < 1) {
      throw new IllegalArgumentException("Chunk height cannot be less than 1");
    }

    LOG.info("Creating chunk at a point [{}]", center);

    //1. create tileIds which will be part of the chunk
    final List<TileId> chunkTiles = new ArrayList<>();
    final int tilesToLeft = width / 2;
    //One is added because tiles to right include the same column as center
    final int tilesToRight = tilesToLeft + 1;
    final int tilesBelow = height / 2;
    //One is added because tiles above include the same row as center
    final int tilesAbove = tilesBelow + 1;
    for (int i = -tilesToLeft; i < tilesToRight; i++) {
      for (int j = -tilesBelow; j < tilesAbove; j++) {
        chunkTiles.add(new TileId(center.getX() + i, center.getY() + j));
      }
    }

    LOG.info("Will generate chunk from [{}, {}] to [{}, {}]",
        center.getX() - tilesToLeft, center.getY() - tilesAbove,
        center.getX() + tilesToRight, center.getY() + tilesBelow
    );

    //3. remove those tileIds which already exist
    chunkTiles.removeIf(tileId -> worldRepository.getTile(tileId).isPresent());

    //4. create Tile objects
    final List<Tile> tiles = chunkTiles
                                 .stream()
                                 .map(Tile::new)
                                 .collect(Collectors.toList());

    eventPublisher.publishEvent(new WorldChunkCreatedEvent(tiles));

    //5. nothing more for now, just save. forests/mountains/etc soon to come
    return worldRepository.addTiles(tiles);
  }

  @Override
  public boolean tileExists(final TileId tileId) {
    return worldRepository.getTile(tileId).isPresent();
  }

}
