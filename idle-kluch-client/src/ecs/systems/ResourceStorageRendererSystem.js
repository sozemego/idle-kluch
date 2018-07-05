import Node from "../Node";
import { ResourceStorageComponent } from "../components/ResourceStorageComponent";
import { PhysicsComponent } from "../components/PhysicsComponent";


export class ResourceStorageRendererSystem {

  constructor(engine, textGroup, textFactory, getSelectedEntity) {
    this.engine = engine;
    this.node = Node.of([ PhysicsComponent, ResourceStorageComponent ]);
    this.textGroup = textGroup;
    this.textFactory = textFactory;
    this.texts = {};
    this.getSelectedEntity = getSelectedEntity;
  }

  getEngine() {
    return this.engine;
  }

  shouldUpdate = () => true;

  update = (delta) => {
    const selectedEntity = this.getSelectedEntity() || { getId: () => null };

    this.getEngine()
      .getEntitiesByNode(this.node)
      .map(entity => {
        const text = this.getText(entity.getId());
        text.kill();
        return entity;
      })
      .filter(entity => entity.getId() === selectedEntity.getId())
      .forEach((entity) => this.updateEntity(entity, delta));
  }

  updateEntity = (entity, delta) => {
    const text = this.getText(entity.getId());
    text.revive();
    const physicsComponent = entity.getComponent(PhysicsComponent);
    const storageComponent = entity.getComponent(ResourceStorageComponent);

    text.x = physicsComponent.getX() + physicsComponent.getWidth();
    text.y = physicsComponent.getY();
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