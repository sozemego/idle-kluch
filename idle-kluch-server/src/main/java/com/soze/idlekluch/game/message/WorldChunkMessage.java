package com.soze.idlekluch.game.message;

import com.soze.idlekluch.world.entity.Tile;

import java.util.List;
import java.util.Objects;

public class WorldChunkMessage extends OutgoingMessage {

  private final List<Tile> tiles;

  public WorldChunkMessage(final List<Tile> tiles) {
    super(OutgoingMessageType.WORLD_CHUNK);
    this.tiles = Objects.requireNonNull(tiles);
  }

  public List<Tile> getTiles() {
    return tiles;
  }
}
