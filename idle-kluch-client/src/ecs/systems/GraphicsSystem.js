import { EntitySystem } from "../EntitySystem";
import { GraphicsComponent } from "../components/GraphicsComponent";
import { PhysicsComponent } from "../components/PhysicsComponent";
import { GRAPHICS_RENDER_NODE } from "../nodes";

export class GraphicsSystem extends EntitySystem {
  constructor(engine) {
    super(engine);
    this.node = GRAPHICS_RENDER_NODE;
  }

  update = delta => {
    const entities = this.getEngine().getEntitiesByNode(this.node);
    entities.forEach(entity => {
      const graphicsComponent = entity.getComponent(GraphicsComponent);
      const sprite = graphicsComponent.getSprite();
      if(!sprite.idleTweening) {
        const physicsComponent = entity.getComponent(PhysicsComponent);
        graphicsComponent.setSpritePosition(
          physicsComponent.getX(),
          physicsComponent.getY(),
        );
      }
    });
  };
}
