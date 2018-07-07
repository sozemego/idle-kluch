package com.soze.idlekluch.game.engine.components;


import java.util.Objects;

/**
 * Display name of the entity.
 */
public class NameComponent extends BaseComponent {

  private String name;

  public NameComponent() {
    super(ComponentType.NAME);
  }

  public NameComponent(final String name) {
    this();
    this.name = Objects.requireNonNull(name);
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = Objects.requireNonNull(name);
  }

  @Override
  public NameComponent copy() {
    return new NameComponent(getName());
  }

  @Override
  public String toString() {
    return "NameComponent{" +
             "name='" + name + '\'' +
             '}';
  }
}
