package com.soze.idlekluch.world.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Objects;

@Entity
@Table(name = "tiles")
public class Tile {

  @EmbeddedId
  private TileId tileId;

  public Tile() {

  }

  @JsonCreator
  public Tile(@JsonProperty("x") final int x, @JsonProperty("y") final int y) {
    this(new TileId(x, y));
  }

  public Tile(final TileId tileId) {
    this.tileId = Objects.requireNonNull(tileId);
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

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final Tile tile = (Tile) o;
    return Objects.equals(tileId, tile.tileId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tileId);
  }
}
