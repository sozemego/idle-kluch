package com.soze.idlekluch.world.service;

import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.entity.TileId;

import java.awt.*;
import java.util.Map;

public interface WorldService {

  Map<Point, Tile> getAllTiles();

  int getMaxWorldWidth();

  int getMaxWorldHeight();

  int getTileSize();

  /**
   * Creates a world chunk at a given coordinates.
   */
  void createWorldChunk(final TileId startingPoint);

}
