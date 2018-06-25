import Node from "../Node";
import { ResourceHarvesterComponent } from "../components/ResourceHarvesterComponent";
import { ResourceStorageComponent } from "../components/ResourceStorageComponent";
import { HARVESTING_STATE } from "../constants";
import { PhysicsComponent } from "../components/PhysicsComponent";

export class ResourceHarvesterRendererSystem {

  constructor(engine, graphics) {
    this.engine = engine;
    this.graphics = graphics;
    this.node = Node.of([ ResourceHarvesterComponent, PhysicsComponent ]);
  }

  update = delta => {
    const entities = this.getEngine().getEntitiesByNode(this.node);
    entities.forEach(entity => this.updateEntity(entity, delta));
  };

  shouldUpdate = () => {
    return true;
  };

  updateEntity = (entity, delta) => {
    const harvesterComponent = entity.getComponent(ResourceHarvesterComponent);
    const physicsComponent = entity.getComponent(PhysicsComponent);

    const g = this.graphics;

    g.lineStyle(1, 0x0000FF, 1);
    g.drawRect(
      physicsComponent.getX(),
      physicsComponent.getY() + physicsComponent.getHeight(),
      physicsComponent.getWidth() * harvesterComponent.getProgress(),
      12
    );

    if(harvesterComponent.isFinished()) {
      g.clear();
    }
  };

  getEngine = () => {
    return this.engine;
  };

}

