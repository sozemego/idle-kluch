package com.soze.idlekluch.game.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.soze.idlekluch.world.entity.Tile;

import java.util.List;
import java.util.Objects;

public class WorldChunkMessage extends OutgoingMessage {

  private final List<Tile> tiles;

  @JsonCreator
  public WorldChunkMessage(@JsonProperty("tiles") final List<Tile> tiles) {
    super(OutgoingMessageType.WORLD_CHUNK);
    this.tiles = Objects.requireNonNull(tiles);
  }

  public List<Tile> getTiles() {
    return tiles;
  }
}
