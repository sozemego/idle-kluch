package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Represents a cost (in idle bucks) of an entity (to be constructed/recruited/etc).
 */
@Entity
@Table(name = "cost_components")
public class CostComponent extends BaseComponent {

  @Column(name = "idle_bucks")
  private int idleBucks;

  public CostComponent(final EntityUUID entityId, final int idleBucks) {
    this();
    setEntityId(entityId);
    this.idleBucks = idleBucks;
  }

  public CostComponent() {
    super(ComponentType.COST);
  }

  public int getIdleBucks() {
    return idleBucks;
  }

  public void setIdleBucks(final int idleBucks) {
    this.idleBucks = idleBucks;
  }

  @Override
  public CostComponent copy() {
    return new CostComponent(getEntityId(), getIdleBucks());
  }

  @Override
  public String toString() {
    return "CostComponent{" +
               "idleBucks=" + idleBucks +
               '}';
  }
}
