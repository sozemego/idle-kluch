package com.soze.idlekluch.game.engine.components;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.soze.idlekluch.game.engine.components.resourceharvester.ResourceHarvesterComponent;
import com.soze.idlekluch.game.entity.*;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeStringType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.Objects;

@MappedSuperclass
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "type",
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
@TypeDefs({
  @TypeDef(name = "string-array", typeClass = StringArrayType.class),
  @TypeDef(name = "int-array", typeClass = IntArrayType.class),
  @TypeDef(name = "json", typeClass = JsonStringType.class),
  @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
  @TypeDef(name = "jsonb-node", typeClass = JsonNodeBinaryType.class),
  @TypeDef(name = "json-node", typeClass = JsonNodeStringType.class),
  @TypeDef(name = "BuildableComponentType", typeClass = BuildableComponentType.class),
  @TypeDef(name = "GraphicsComponentType", typeClass = GraphicsComponentType.class),
  @TypeDef(name = "NameComponentType", typeClass = NameComponentType.class),
  @TypeDef(name = "OwnershipComponentType", typeClass = OwnershipComponentType.class),
  @TypeDef(name = "PhysicsComponentType", typeClass = PhysicsComponentType.class),
  @TypeDef(name = "CostComponentType", typeClass = CostComponentType.class),
  @TypeDef(name = "StaticOccupySpaceComponentType", typeClass = StaticOccupySpaceComponentType.class),
  @TypeDef(name = "ResourceSourceComponentType", typeClass = ResourceSourceComponentType.class),
  @TypeDef(name = "ResourceHarvesterComponentType", typeClass = ResourceHarvesterComponentType.class),
  @TypeDef(name = "ResourceStorageComponentType", typeClass = ResourceStorageComponentType.class)
})
public abstract class BaseComponent {

  @Transient
  private ComponentType componentType;

  public BaseComponent() {

  }

  public BaseComponent(final ComponentType componentType) {
    this.componentType = Objects.requireNonNull(componentType);
  }

  @JsonProperty("type")
  public ComponentType getComponentType() {
    return componentType;
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
             ", componentType=" + componentType +
             '}';
  }
}
