package com.soze.idlekluch.world.entity;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.*;

@Entity
@Table(name = "trees")
public class Tree {

  @EmbeddedId
  @AttributeOverride(name = "id", column = @Column(name = "tree_id"))
  private EntityUUID treeId;

  @Column(name = "x")
  private int x;
  @Column(name = "y")
  private int y;

  @Column(name = "definition_id")
  private String definitionId;

  @Column(name = "yield")
  private float yield;

  public Tree() {

  }

  public EntityUUID getTreeId() {
    return treeId;
  }

  public void setTreeId(final EntityUUID treeId) {
    this.treeId = treeId;
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
