package com.soze.idlekluch.kingdom.entity;

import com.soze.idlekluch.user.entity.User;
import com.soze.idlekluch.utils.jpa.EntityUUID;

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

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  @OneToOne
  @JoinColumn(name = "owner")
  private User owner;

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

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

}
