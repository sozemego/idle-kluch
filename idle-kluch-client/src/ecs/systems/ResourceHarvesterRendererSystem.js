import { ResourceHarvesterComponent } from "../components/ResourceHarvesterComponent";
import { PhysicsComponent } from "../components/PhysicsComponent";
import { Engine as Entity } from "../Engine";
import { RESOURCE_HARVESTER_NODE } from "../nodes";

export class ResourceHarvesterRendererSystem {

  constructor(engine, spriteFactory, getSelectedEntity, entitySelector, jumpingSpriteFactory, getResourceById) {
    this.engine = engine;
    this.spriteFactory = spriteFactory;
    this.node = RESOURCE_HARVESTER_NODE;
    this.sprites = {};
    this.getSelectedEntity = getSelectedEntity;
    this.entitySelector = entitySelector;
    this.jumpingSpriteFactory = jumpingSpriteFactory;
    this.getResourceById = getResourceById;
  }

  update = delta => {
    this.entitySelector.clear();
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

    if (selectedEntity.getId() === entity.getId()) {
      harvesterComponent.getSlots().forEach(this.renderSource);
    }

    if (harvesterComponent.justFinished) {
      harvesterComponent.justFinished = false;
      const resource = this.getResourceById(harvesterComponent.getResource());
			this.jumpingSpriteFactory(
			  resource.name,
				{
					x: physicsComponent.getX() + physicsComponent.getWidth() / 2,
					y: physicsComponent.getY(),
				});
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

  renderSource = (slot) => {
    const sourceEntity = this.getEngine().getEntityById(slot.sourceId);
    const physicsComponent = sourceEntity.getComponent(PhysicsComponent);
    this.entitySelector.clear();
    this.entitySelector.lineStyle(2, 0xdd00dd, 1);
    this.entitySelector.drawRect(
      physicsComponent.getX(), physicsComponent.getY(),
      physicsComponent.getWidth(), physicsComponent.getHeight(),
    );
  };


  getEngine = () => {
    return this.engine;
  };

}

