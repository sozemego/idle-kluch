package com.soze.idlekluch.game.engine.nodes;

import com.soze.idlekluch.game.engine.components.OwnershipComponent;
import com.soze.idlekluch.game.engine.components.PhysicsComponent;
import com.soze.klecs.node.Node;

public interface Nodes {

  Node PHYSICAL_NODE = Node.of(PhysicsComponent.class);
  Node OWNERSHIP = Node.of(OwnershipComponent.class);

}
