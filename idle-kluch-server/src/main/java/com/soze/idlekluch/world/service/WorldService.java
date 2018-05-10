package com.soze.idlekluch.world.service;

import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.entity.TileId;

import java.util.List;
import java.util.Map;

public interface WorldService {

  /**
   * Length of edge of a tile. Tiles are squares.
   */
  int TILE_SIZE = 128;

  Map<TileId, Tile> getAllTiles();

  /**
   * Creates a world chunk at a given coordinates.
   * The chunk will be 15x15 tiles large, however all tiles that already exist in this region will be left undisturbed,
   * only new ones will be created.
   * @return List of newly created tiles
   */
  List<Tile> createWorldChunk(final TileId startingPoint);

}
