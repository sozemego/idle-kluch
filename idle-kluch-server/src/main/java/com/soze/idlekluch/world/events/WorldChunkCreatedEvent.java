package com.soze.idlekluch.world.events;

import com.soze.idlekluch.world.entity.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorldChunkCreatedEvent {

  private final List<Tile> tiles;

  public WorldChunkCreatedEvent(final List<Tile> tiles) {
    this.tiles = new ArrayList<>(Objects.requireNonNull(tiles));
  }

  public List<Tile> getTiles() {
    return tiles;
  }
}
