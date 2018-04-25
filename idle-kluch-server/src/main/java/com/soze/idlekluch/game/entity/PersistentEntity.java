package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.GraphicsComponent;
import com.soze.idlekluch.game.engine.components.PhysicsComponent;
import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.*;

@Entity
@Table(name = "entities")
@SecondaryTables({
               @SecondaryTable(name = "graphics_components", pkJoinColumns = @PrimaryKeyJoinColumn(name="entity_id", referencedColumnName = "entity_id")),
               @SecondaryTable(name = "physics_components", pkJoinColumns = @PrimaryKeyJoinColumn(name="entity_id", referencedColumnName = "entity_id")),
               @SecondaryTable(name = "ownership_components", pkJoinColumns = @PrimaryKeyJoinColumn(name="entity_id", referencedColumnName = "entity_id"))
})
public class PersistentEntity {

  @EmbeddedId
  @AttributeOverride(name = "id", column = @Column(name = "entity_id"))
  private EntityUUID entityId;

  @Embedded
  @AttributeOverrides({
                        @AttributeOverride(name = "asset", column = @Column(table = "graphics_components"))
  })
  private GraphicsComponent graphicsComponent;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "x", column = @Column(table = "physics_components")),
    @AttributeOverride(name = "y", column = @Column(table = "physics_components")),
    @AttributeOverride(name = "width", column = @Column(table = "physics_components")),
    @AttributeOverride(name = "height", column = @Column(table = "physics_components"))
  })
  private PhysicsComponent physicsComponent;

//  @Embedded
//  @AttributeOverrides({
//    @AttributeOverride(name = "owner_id", column = @Column(table = "ownership_components"))
//  })
//  private OwnershipComponent ownershipComponent;

  public PersistentEntity() {

  }

  public EntityUUID getEntityId() {
    return entityId;
  }

  public void setEntityId(final EntityUUID entityId) {
    this.entityId = entityId;
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

//  public OwnershipComponent getOwnershipComponent() {
//    return ownershipComponent;
//  }
//
//  public void setOwnershipComponent(final OwnershipComponent ownershipComponent) {
//    this.ownershipComponent = ownershipComponent;
//  }
}
