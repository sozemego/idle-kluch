package com.soze.idlekluch.world.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "tiles")
public class Tile {

  @EmbeddedId
  private TileId tileId;

  public Tile() {

  }

  @JsonIgnore
  public TileId getTileId() {
    return tileId;
  }

  @Transient
  public int getX() {
    return tileId.getX();
  }

  @Transient
  public int getY() {
    return tileId.getY();
  }

  public void setTileId(final TileId tileId) {
    this.tileId = tileId;
  }

}
