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
  List<Tile> createWorldChunk(final TileId center);

  /**
   * Creates a chunk around center tile.
   * Given width and height will be the width and height of the new chunk. Keep in mind that if either width or height
   * is an even number, the actual width and height will be the next odd number (so width or height + 1).
   * @return List of newly created tiles
   */
  List<Tile> createWorldChunk(final TileId center, final int width, final int height);

}
