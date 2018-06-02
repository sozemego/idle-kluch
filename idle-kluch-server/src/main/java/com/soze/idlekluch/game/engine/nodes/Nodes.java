package com.soze.idlekluch.game.engine.nodes;

import com.soze.idlekluch.game.engine.components.*;
import com.soze.klecs.node.Node;

public interface Nodes {

  Node PHYSICAL_NODE = Node.of(PhysicsComponent.class);
  Node OWNERSHIP = Node.of(OwnershipComponent.class);
  Node BUILDABLE = Node.of(BuildableComponent.class);
  Node RESOURCE_SOURCE = Node.of(ResourceSourceComponent.class);
  Node OCCUPY_SPACE = Node.of(PhysicsComponent.class, StaticOccupySpaceComponent.class);

  Node BUILDING = Node.of(PhysicsComponent.class, OwnershipComponent.class, BuildableComponent.class, StaticOccupySpaceComponent.class);

}
