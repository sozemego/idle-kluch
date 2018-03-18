package com.soze.idlekluch.world.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TileId implements Serializable {

  @Column(name = "x", nullable = false)
  private int x;

  @Column(name = "y", nullable = false)
  private int y;

  public TileId() {

  }

  public TileId(final int x, final int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public void setX(final int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(final int y) {
    this.y = y;
  }

  public static TileId from(final int x, final int y) {
    return new TileId(x, y);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final TileId tileId = (TileId) o;
    return getX() == tileId.getX() &&
      getY() == tileId.getY();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getX(), getY());
  }
}
