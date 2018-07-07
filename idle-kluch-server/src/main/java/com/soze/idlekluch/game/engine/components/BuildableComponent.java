package com.soze.idlekluch.game.engine.components;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Specifies that this component is buildable.
 * Later this will be extended for sure, this component is now a marker.
 */
@Entity
@Table(name = "buildable_components")
public class BuildableComponent extends BaseComponent {

  public BuildableComponent() {
    super(ComponentType.BUILDABLE);
  }

  @Override
  public BuildableComponent copy() {
    return new BuildableComponent();
  }

  @Override
  public String toString() {
    return "BuildableComponent{}";
  }
}
