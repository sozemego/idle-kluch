package com.soze.idlekluch.game.engine.components;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A component for objects on which you cannot travel or build.
 */
@Entity
@Table(name = "static_occupy_space_components")
public class StaticOccupySpaceComponent extends BaseComponent {

  public StaticOccupySpaceComponent() {
    super(ComponentType.STATIC_OCCUPY_SPACE);
  }

  @Override
  public StaticOccupySpaceComponent copy() {
    return new StaticOccupySpaceComponent();
  }

  @Override
  public String toString() {
    return "StaticOccupySpaceComponent{}";
  }
}
