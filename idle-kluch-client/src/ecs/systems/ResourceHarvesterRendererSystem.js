import Node from "../Node";
import { ResourceHarvesterComponent } from "../components/ResourceHarvesterComponent";
import { PhysicsComponent } from "../components/PhysicsComponent";
import { Engine as Entity } from "../Engine";

export class ResourceHarvesterRendererSystem {

  constructor(engine, spriteFactory, getSelectedEntity) {
    this.engine = engine;
    this.spriteFactory = spriteFactory;
    this.node = Node.of([ ResourceHarvesterComponent, PhysicsComponent ]);
    this.sprites = {};
    this.getSelectedEntity = getSelectedEntity;
  }

  update = delta => {
    const entities = this.getEngine().getEntitiesByNode(this.node);
    const selectedEntity = this.getSelectedEntity() || Entity.nullInstance();
    entities.forEach(entity => this.updateEntity(entity, selectedEntity));
  };

  shouldUpdate = () => {
    return true;
  };

  updateEntity = (entity, selectedEntity) => {
    const harvesterComponent = entity.getComponent(ResourceHarvesterComponent);
    const physicsComponent = entity.getComponent(PhysicsComponent);
    const sprite = this.getSprite(entity.getId());

    sprite.x = physicsComponent.getX();
    sprite.y = physicsComponent.getY() + physicsComponent.getHeight();
    sprite.width = physicsComponent.getWidth() * harvesterComponent.getProgress();
    sprite.height = 12;

    if(selectedEntity.getId() === entity.getId()) {
      //do something
    }
  };

  getSprite = (id) => {
    let sprite = this.sprites[ id ];
    if (!sprite) {
      sprite = this.spriteFactory.sprite(0, 0, "red_button01");
      this.sprites[ id ] = sprite;
    }
    return sprite;
  };

  getEngine = () => {
    return this.engine;
  };

}

