package com.soze.idlekluch.game.entity;

import com.soze.idlekluch.game.engine.components.GraphicsComponent;
import com.soze.idlekluch.game.engine.components.OwnershipComponent;
import com.soze.idlekluch.game.engine.components.PhysicsComponent;
import com.soze.idlekluch.utils.jpa.EntityUUID;

import javax.persistence.*;

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

}
