package com.soze.idlekluch.world.repository;

import com.soze.idlekluch.world.entity.Tile;

import java.util.List;

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

}
