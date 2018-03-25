package com.soze.idlekluch.world.repository;

import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.idlekluch.world.entity.Tile;

import java.util.List;
import java.util.Optional;

public interface WorldRepository {

  /**
   * Returns all tiles in the database.
   * @return
   */
  List<Tile> getAllTiles();

  /**
   * Adds a new tile.
   * @param tile
   */
  public void addTile(final Tile tile);

  public void addTiles(final List<Tile> tiles);

  List<Resource> getAllAvailableResources();

  void addResource(final Resource resource);

  Optional<Resource> getResource(final String name);

}
