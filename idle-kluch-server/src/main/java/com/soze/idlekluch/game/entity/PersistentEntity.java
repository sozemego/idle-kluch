package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.*;
import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "entities")
public class PersistentEntity {

  @EmbeddedId
  @AttributeOverride(name = "id", column = @Column(name = "entity_id"))
  private EntityUUID entityId;

  @Column(name = "template")
  private boolean template;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "entity_id")
  private GraphicsComponent graphicsComponent;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "entity_id")
  private PhysicsComponent physicsComponent;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "entity_id")
  private OwnershipComponent ownershipComponent;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "entity_id")
  private StaticOccupySpaceComponent staticOccupySpaceComponent;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "entity_id")
  private NameComponent nameComponent;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "entity_id")
  private BuildableComponent buildableComponent;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "entity_id")
  private CostComponent costComponent;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "entity_id")
  private ResourceSourceComponent resourceSourceComponent;

  public PersistentEntity() {

  }

  public EntityUUID getEntityId() {
    return entityId;
  }

  public void setEntityId(final EntityUUID entityId) {
    this.entityId = entityId;
  }

  public boolean isTemplate() {
    return template;
  }

  public void setTemplate(final boolean template) {
    this.template = template;
  }

  public GraphicsComponent getGraphicsComponent() {
    return graphicsComponent;
  }

  public void setGraphicsComponent(final GraphicsComponent graphicsComponent) {
    this.graphicsComponent = graphicsComponent;
  }

  public PhysicsComponent getPhysicsComponent() {
    return physicsComponent;
  }

  public void setPhysicsComponent(final PhysicsComponent physicsComponent) {
    this.physicsComponent = physicsComponent;
  }

  public OwnershipComponent getOwnershipComponent() {
    return ownershipComponent;
  }

  public void setOwnershipComponent(final OwnershipComponent ownershipComponent) {
    this.ownershipComponent = ownershipComponent;
  }

  public StaticOccupySpaceComponent getStaticOccupySpaceComponent() {
    return staticOccupySpaceComponent;
  }

  public void setStaticOccupySpaceComponent(final StaticOccupySpaceComponent staticOccupySpaceComponent) {
    this.staticOccupySpaceComponent = staticOccupySpaceComponent;
  }

  public NameComponent getNameComponent() {
    return nameComponent;
  }

  public void setNameComponent(final NameComponent nameComponent) {
    this.nameComponent = nameComponent;
  }

  public BuildableComponent getBuildableComponent() {
    return buildableComponent;
  }

  public void setBuildableComponent(final BuildableComponent buildableComponent) {
    this.buildableComponent = buildableComponent;
  }

  public CostComponent getCostComponent() {
    return costComponent;
  }

  public void setCostComponent(final CostComponent costComponent) {
    this.costComponent = costComponent;
  }

  public ResourceSourceComponent getResourceSourceComponent() {
    return resourceSourceComponent;
  }

  public void setResourceSourceComponent(final ResourceSourceComponent resourceSourceComponent) {
    this.resourceSourceComponent = resourceSourceComponent;
  }

  @Transient
  public List<BaseComponent> getAllComponents() {
    return Stream.of(
      getBuildableComponent(),
      getCostComponent(),
      getGraphicsComponent(),
      getNameComponent(),
      getOwnershipComponent(),
      getPhysicsComponent(),
      getStaticOccupySpaceComponent(),
      getResourceSourceComponent()
    )
    .filter(Objects::nonNull)
    .collect(Collectors.toList());
  }

}
