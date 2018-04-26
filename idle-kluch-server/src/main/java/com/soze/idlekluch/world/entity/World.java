package com.soze.idlekluch.world.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "world")
public class World {

  @Id
  @Column(name = "world_id")
  private long worldId = 1;

  public long getWorldId() {
    return worldId;
  }

  public void setWorldId(final long worldId) {
    this.worldId = worldId;
  }
}
