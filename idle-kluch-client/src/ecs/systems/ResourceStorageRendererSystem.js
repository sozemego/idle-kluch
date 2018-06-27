import Node from "../Node";
import { ResourceStorageComponent } from "../components/ResourceStorageComponent";
import { PhysicsComponent } from "../components/PhysicsComponent";


export class ResourceStorageRendererSystem {

  constructor(engine, textGroup, textFactory) {
    this.engine = engine;
    this.node = Node.of([ PhysicsComponent, ResourceStorageComponent ]);
    this.textGroup = textGroup;
    this.textFactory = textFactory;
    this.texts = {};
  }

  getEngine() {
    return this.engine;
  }

  shouldUpdate = () => true;

  update = (delta) => {
    this.getEngine()
      .getEntitiesByNode(this.node)
      .forEach((entity) => this.updateEntity(entity, delta));
  }

  updateEntity = (entity, delta) => {
    const text = this.getText(entity.getId());
    const physicsComponent = entity.getComponent(PhysicsComponent);
    const storageComponent = entity.getComponent(ResourceStorageComponent);

    text.x = physicsComponent.getX();
    text.y = physicsComponent.getY() - 16;
    text.text = storageComponent.getResources().length + "/" + storageComponent.getCapacity();

  }

  getText = (id) => {
    let text = this.texts[ id ];
    if (!text) {
      text = this.textFactory.text(0, 0, "", { font: "16px Arial", fill: "rgba(255, 255, 255, 0.9)", stroke: "black" });
      this.textGroup.add(text);
      this.texts[ id ] = text;
    }
    return text;
  }

}