package com.soze.idlekluch.kingdom.entity;

import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "buildings")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Building {

  @EmbeddedId
  @AttributeOverride(name = "id", column = @Column(name = "building_id"))
  private EntityUUID buildingId;

  @Column(name = "created_at")
  private Timestamp createdAt;

  @Column(name = "name")
  private String name;

  @OneToOne
  @JoinColumn
  private Kingdom kingdom;

  @Column(name = "x")
  private int x;

  @Column(name = "y")
  private int y;

  public Building() {

  }

  public EntityUUID getBuildingId() {
    return buildingId;
  }

  public void setBuildingId(final EntityUUID buildingId) {
    this.buildingId = buildingId;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(final Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Kingdom getKingdom() {
    return kingdom;
  }

  public void setKingdom(final Kingdom kingdom) {
    this.kingdom = kingdom;
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
}
