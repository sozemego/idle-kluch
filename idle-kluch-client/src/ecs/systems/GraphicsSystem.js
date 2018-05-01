import { EntitySystem } from "../EntitySystem";
import { GraphicsComponent } from "../components/GraphicsComponent";
import { PhysicsComponent } from "../components/PhysicsComponent";
import { Node } from "../Node";

export class GraphicsSystem extends EntitySystem {
  constructor(engine) {
    super(engine);
    this.node = Node.of([ GraphicsComponent, PhysicsComponent ]);
  }

  update = delta => {
    const entities = this.getEngine().getEntitiesByNode(this.node);
    entities.forEach(entity => {
      const graphicsComponent = entity.getComponent(GraphicsComponent);
      const physicsComponent = entity.getComponent(PhysicsComponent);
      graphicsComponent.setSpritePosition(
        physicsComponent.getX(),
        physicsComponent.getY()
      );
    });
  };
}
