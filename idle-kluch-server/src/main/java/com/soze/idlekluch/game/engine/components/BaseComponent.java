package com.soze.idlekluch.game.engine.components;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
@TypeDefs({
  @TypeDef(name = "string-array", typeClass = StringArrayType.class),
  @TypeDef(name = "int-array", typeClass = IntArrayType.class),
  @TypeDef(name = "json", typeClass = JsonStringType.class),
  @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
  @TypeDef(name = "jsonb-node", typeClass = JsonNodeBinaryType.class),
  @TypeDef(name = "json-node", typeClass = JsonNodeStringType.class),
  @TypeDef(name = "BuildableComponentUserType", typeClass = BuildableComponentUserType.class),
  @TypeDef(name = "GraphicsComponentUserType", typeClass = GraphicsComponentUserType.class),
  @TypeDef(name = "NameComponentUserType", typeClass = NameComponentUserType.class),
  @TypeDef(name = "OwnershipComponentUserType", typeClass = OwnershipComponentUserType.class),
  @TypeDef(name = "PhysicsComponentUserType", typeClass = PhysicsComponentUserType.class),
  @TypeDef(name = "CostComponentUserType", typeClass = CostComponentUserType.class),
  @TypeDef(name = "StaticOccupySpaceComponentUserType", typeClass = StaticOccupySpaceComponentUserType.class),
  @TypeDef(name = "ResourceSourceComponentUserType", typeClass = ResourceSourceComponentUserType.class),
  @TypeDef(name = "ResourceHarvesterComponentUserType", typeClass = ResourceHarvesterComponentUserType.class),
  @TypeDef(name = "ResourceStorageComponentUserType", typeClass = ResourceStorageComponentUserType.class),
  @TypeDef(name = "ResourceSellerComponentUserType", typeClass = ResourceSellerComponentUserType.class),
})
@JsonInclude(Include.NON_NULL)
public abstract class BaseComponent {

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
    RESOURCE_STORAGE, SELLER
  }

  @Override
  public String toString() {
    return "BaseComponent{" +
             ", componentType=" + componentType +
             '}';
  }
}
