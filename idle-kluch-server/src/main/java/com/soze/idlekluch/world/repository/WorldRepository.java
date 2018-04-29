package com.soze.idlekluch.world.repository;

import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.entity.World;

import java.util.List;
import java.util.Optional;

public interface WorldRepository {

  Optional<World> getCurrentWorld();

  void saveWorld(final World world);

  /**
   * Returns all tiles in the database.
   */
  List<Tile> getAllTiles();

  /**
   * Adds a new tile.
   */
  public void addTile(final Tile tile);

  public void addTiles(final List<Tile> tiles);

  void removeTiles(final List<Tile> tile);

  List<Resource> getAllAvailableResources();

  void addResource(final Resource resource);

  Optional<Resource> getResource(final String name);

}
