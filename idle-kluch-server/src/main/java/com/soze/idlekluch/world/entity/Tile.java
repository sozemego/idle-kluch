package com.soze.idlekluch.world.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tiles")
public class Tile {

  @EmbeddedId
  private TileId tileId;

  public Tile() {

  }

  public TileId getTileId() {
    return tileId;
  }

  public void setTileId(final TileId tileId) {
    this.tileId = tileId;
  }
}
