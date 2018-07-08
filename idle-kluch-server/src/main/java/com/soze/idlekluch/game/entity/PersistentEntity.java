package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.components.*;
import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceHarvesterComponent;
import org.hibernate.annotations.Type;

import javax.persistence.*;
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

  @Column(name = "graphics_component")
  @Type(type = "GraphicsComponentUserType")
  private GraphicsComponent graphicsComponent;

  @Column(name = "physics_component")
  @Type(type = "PhysicsComponentUserType")
  private PhysicsComponent physicsComponent;

  @Column(name = "ownership_component")
  @Type(type = "OwnershipComponentUserType")
  private OwnershipComponent ownershipComponent;

  @Column(name = "static_occupy_space_component")
  @Type(type = "StaticOccupySpaceComponentUserType")
  private StaticOccupySpaceComponent staticOccupySpaceComponent;

  @Column(name = "name_component")
  @Type(type = "NameComponentUserType")
  private NameComponent nameComponent;

  @Column(name = "buildable_component")
  @Type(type = "BuildableComponentUserType")
  private BuildableComponent buildableComponent;

  @Column(name = "cost_component")
  @Type(type = "CostComponentUserType")
  private CostComponent costComponent;

  @Column(name = "resource_source_component")
  @Type(type = "ResourceSourceComponentUserType")
  private ResourceSourceComponent resourceSourceComponent;

  @Column(name = "resource_harvester_component")
  @Type(type = "ResourceHarvesterComponentUserType")
  private ResourceHarvesterComponent resourceHarvesterComponent;

  @Column(name = "resource_storage_component")
  @Type(type = "ResourceStorageComponentUserType")
  private ResourceStorageComponent resourceStorageComponent;


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

  public ResourceHarvesterComponent getResourceHarvesterComponent() {
    return resourceHarvesterComponent;
  }

  public void setResourceHarvesterComponent(final ResourceHarvesterComponent resourceHarvesterComponent) {
    this.resourceHarvesterComponent = resourceHarvesterComponent;
  }

  public ResourceStorageComponent getResourceStorageComponent() {
    return resourceStorageComponent;
  }

  public void setResourceStorageComponent(final ResourceStorageComponent resourceStorageComponent) {
    this.resourceStorageComponent = resourceStorageComponent;
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
      getResourceSourceComponent(),
      getResourceHarvesterComponent(),
      getResourceStorageComponent()
    )
    .filter(Objects::nonNull)
    .collect(Collectors.toList());
  }

}
