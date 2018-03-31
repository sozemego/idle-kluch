package com.soze.idlekluch.kingdom.entity;

import com.soze.idlekluch.kingdom.dto.BuildingDto.BuildingType;
import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "buildings")
@Inheritance(strategy = InheritanceType.JOINED)
public class Building {

  @EmbeddedId
  @AttributeOverride(name = "id", column = @Column(name = "building_id"))
  private EntityUUID buildingId;

  @Column(name = "definition_id")
  private String definitionId;

  @Column(name = "created_at")
  private Timestamp createdAt;

  @Column(name = "name")
  private String name;

  @OneToOne
  @JoinColumn(name = "kingdom_id")
  private Kingdom kingdom;

  @Column(name = "x")
  private int x;

  @Column(name = "y")
  private int y;

  @Transient
  private BuildingType buildingType;

  public Building() {

  }

  public EntityUUID getBuildingId() {
    return buildingId;
  }

  public void setBuildingId(final EntityUUID buildingId) {
    this.buildingId = buildingId;
  }

  public String getDefinitionId() {
    return definitionId;
  }

  public void setDefinitionId(final String definitionId) {
    this.definitionId = definitionId;
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

  public BuildingType getBuildingType() {
    //to make sure this property is set
    Objects.requireNonNull(buildingType, "Building type cannot be null, set it first!");
    return buildingType;
  }

  public void setBuildingType(final BuildingType buildingType) {
    this.buildingType = buildingType;
  }
}
