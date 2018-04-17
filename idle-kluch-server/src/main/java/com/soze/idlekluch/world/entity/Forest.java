package com.soze.idlekluch.world.entity;

import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.*;

@Entity
@Table(name = "forests")
public class Forest {

  @EmbeddedId
  @AttributeOverride(name = "id", column = @Column(name = "forest_id"))
  private EntityUUID forestId;

  @Column(name = "x")
  private int x;
  @Column(name = "y")
  private int y;

  @Column(name = "definition_id")
  private String definitionId;

  @Column(name = "yield")
  private float yield;

  public Forest() {

  }

  public EntityUUID getForestId() {
    return forestId;
  }

  public void setForestId(final EntityUUID forestId) {
    this.forestId = forestId;
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

  public String getDefinitionId() {
    return definitionId;
  }

  public void setDefinitionId(final String definitionId) {
    this.definitionId = definitionId;
  }

  public float getYield() {
    return yield;
  }

  public void setYield(final float yield) {
    this.yield = yield;
  }
}
