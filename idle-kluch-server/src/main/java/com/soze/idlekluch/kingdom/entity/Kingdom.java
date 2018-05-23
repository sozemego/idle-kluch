package com.soze.idlekluch.kingdom.entity;

import com.soze.idlekluch.user.entity.User;
import com.soze.idlekluch.utils.jpa.EntityUUID;
import com.soze.idlekluch.world.entity.TileId;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "kingdoms")
public class Kingdom {

  @EmbeddedId
  @AttributeOverride(name = "id", column = @Column(name = "kingdom_id"))
  private EntityUUID kingdomId;

  @Column(name = "name")
  private String name;

  @Column(name = "idle_bucks")
  private long idleBucks;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  @OneToOne
  @JoinColumn(name = "owner")
  private User owner;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "x", column = @Column(name="starting_point_x")),
    @AttributeOverride(name = "y", column = @Column(name="starting_point_y"))
  })
  private TileId startingPoint;

  public Kingdom() {

  }

  public EntityUUID getKingdomId() {
    return kingdomId;
  }

  public void setKingdomId(final EntityUUID kingdomId) {
    this.kingdomId = kingdomId;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(final User owner) {
    this.owner = owner;
  }

  public long getIdleBucks() {
    return idleBucks;
  }

  public void setIdleBucks(final long idleBucks) {
    this.idleBucks = idleBucks;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public TileId getStartingPoint() {
    return startingPoint;
  }

  public void setStartingPoint(final TileId startingPoint) {
    this.startingPoint = startingPoint;
  }

  @Override
  public String toString() {
    return "Kingdom{" +
             "name='" + name + '\'' +
             ", idleBucks=" + idleBucks +
             ", owner=" + owner +
             '}';
  }
}
