import Node from "../Node";
import { ResourceHarvesterComponent } from "../components/ResourceHarvesterComponent";
import { PhysicsComponent } from "../components/PhysicsComponent";

export class ResourceHarvesterRendererSystem {

  constructor(engine, spriteFactory) {
    this.engine = engine;
    this.spriteFactory = spriteFactory;
    this.node = Node.of([ ResourceHarvesterComponent, PhysicsComponent ]);
    this.sprites = {};
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
    const sprite = this.getSprite(entity.getId());

    sprite.x = physicsComponent.getX();
    sprite.y = physicsComponent.getY() + physicsComponent.getHeight();
    sprite.width = physicsComponent.getWidth() * harvesterComponent.getProgress();
    sprite.height = 12;
  };

  getSprite = (id) => {
    let sprite = this.sprites[id];
    if(!sprite) {
      sprite = this.spriteFactory.sprite(0, 0, "red_button01");
      this.sprites[id] = sprite;
    }
    return sprite;
  };

  getEngine = () => {
    return this.engine;
  };

}

