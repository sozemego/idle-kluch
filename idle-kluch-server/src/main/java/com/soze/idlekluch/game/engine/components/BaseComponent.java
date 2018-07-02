package com.soze.idlekluch.game.engine.components;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.soze.idlekluch.core.utils.jpa.EntityUUID;
import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceHarvesterComponent;

import javax.persistence.*;
import java.util.Objects;

@MappedSuperclass
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "componentType",
  visible = true
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = BuildableComponent.class, name = "BUILDABLE"),
  @JsonSubTypes.Type(value = GraphicsComponent.class, name = "GRAPHICS"),
  @JsonSubTypes.Type(value = NameComponent.class, name = "NAME"),
  @JsonSubTypes.Type(value = OwnershipComponent.class, name = "OWNERSHIP"),
  @JsonSubTypes.Type(value = PhysicsComponent.class, name = "PHYSICS"),
  @JsonSubTypes.Type(value = StaticOccupySpaceComponent.class, name = "STATIC_OCCUPY_SPACE"),
  @JsonSubTypes.Type(value = CostComponent.class, name = "COST"),
  @JsonSubTypes.Type(value = ResourceSourceComponent.class, name = "RESOURCE_SOURCE"),
  @JsonSubTypes.Type(value = ResourceHarvesterComponent.class, name = "RESOURCE_HARVESTER"),
  @JsonSubTypes.Type(value = ResourceStorageComponent.class, name = "RESOURCE_STORAGE")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseComponent {

  @EmbeddedId
  @AttributeOverride(name = "id", column = @Column(name = "entity_id"))
  @JsonUnwrapped
  private EntityUUID entityId;

  @Transient
  private ComponentType componentType;

  public BaseComponent() {

  }

  public BaseComponent(final ComponentType componentType) {
    this.componentType = Objects.requireNonNull(componentType);
  }

  public ComponentType getComponentType() {
    return componentType;
  }

  public EntityUUID getEntityId() {
    return entityId;
  }

  public void setEntityId(final EntityUUID entityId) {
    this.entityId = Objects.requireNonNull(entityId);
  }

  /**
   * A method which copies (clones) the component.
   * This is to implement a Template pattern for entities, so that they can be loaded
   * from a file/DB and then copied when they need to be added to the game.
   * This will stop us from having a parallel line of objects which describe how to convert
   * data from file to entity.
   */
  public abstract BaseComponent copy();

  public enum ComponentType {
    PHYSICS, GRAPHICS, OWNERSHIP,
    STATIC_OCCUPY_SPACE, NAME, BUILDABLE,
    COST, RESOURCE_SOURCE, RESOURCE_HARVESTER,
    RESOURCE_STORAGE,
  }

  @Override
  public String toString() {
    return "BaseComponent{" +
             "entityId=" + entityId +
             ", componentType=" + componentType +
             '}';
  }
}
