package com.soze.idlekluch.game.engine.components;

/**
 * Represents a cost (in idle bucks) of an entity (to be constructed/recruited/etc).
 */
public class CostComponent extends BaseComponent {

  private int idleBucks;

  public CostComponent(final int idleBucks) {
    this();
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
    return new CostComponent(getIdleBucks());
  }

  @Override
  public String toString() {
    return "CostComponent{" +
               "idleBucks=" + idleBucks +
               '}';
  }
}
