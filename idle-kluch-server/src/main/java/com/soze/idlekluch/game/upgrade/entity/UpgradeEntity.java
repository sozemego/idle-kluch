package com.soze.idlekluch.game.upgrade.entity;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import javax.persistence.*;

@Entity
@Table(name = "upgrades")
public class UpgradeEntity {

  @Id
  @GeneratedValue
  private int id;

  @AttributeOverride(name = "id", column = @Column(name = "entity_id"))
  private EntityUUID entityId;

  @Column(name = "upgrade_type")
  private String upgradeType;

  @Column(name = "level")
  private int level;

  public UpgradeEntity() { }

  public EntityUUID getEntityId() {
    return entityId;
  }

  public void setEntityId(final EntityUUID entityId) {
    this.entityId = entityId;
  }

  public int getId() {
    return id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public String getUpgradeType() {
    return upgradeType;
  }

  public void setUpgradeType(final String upgradeType) {
    this.upgradeType = upgradeType;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(final int level) {
    this.level = level;
  }
}
