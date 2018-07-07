package com.soze.idlekluch.world.repository;

import com.soze.idlekluch.kingdom.entity.Resource;
import com.soze.idlekluch.world.entity.Tile;
import com.soze.idlekluch.world.entity.TileId;
import com.soze.idlekluch.world.entity.World;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface WorldRepository {

  Optional<World> getCurrentWorld();

  void saveWorld(World world);

  Map<TileId, Tile> getAllTiles();

  Optional<Tile> getTile(TileId tileId);

  public void addTile(Tile tile);

  public List<Tile> addTiles(List<Tile> tiles);

  void removeTiles(List<Tile> tile);

}
