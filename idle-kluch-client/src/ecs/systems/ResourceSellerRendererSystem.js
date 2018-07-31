import { PhysicsComponent } from "../components/PhysicsComponent";
import { Engine as Entity } from "../Engine";
import { RESOURCE_SELLER_NODE } from "../nodes";
import { ResourceSellerComponent } from "../components/ResourceSellerComponent";

export class ResourceSellerRendererSystem {

	constructor(engine, spriteFactory, getSelectedEntity, entitySelector, jumpingSpriteFactory) {
		this.engine = engine;
		this.spriteFactory = spriteFactory;
		this.node = RESOURCE_SELLER_NODE;
		this.sprites = {};
		this.getSelectedEntity = getSelectedEntity;
		this.entitySelector = entitySelector;
		this.jumpingSpriteFactory = jumpingSpriteFactory;
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
		const sellerComponent = entity.getComponent(ResourceSellerComponent);
		const physicsComponent = entity.getComponent(PhysicsComponent);
		const sprite = this.getSprite(entity.getId());

		sprite.x = physicsComponent.getX();
		sprite.y = physicsComponent.getY() + physicsComponent.getHeight();
		sprite.width = physicsComponent.getWidth() * sellerComponent.getSellingProgress();
		sprite.height = 12;

		if (sellerComponent.justSold) {
			this.jumpingSpriteFactory(
				"idle_buck_1",
				{
					x: physicsComponent.getX() + (physicsComponent.getWidth() / 2),
					y: physicsComponent.getY(),
				},
				sellerComponent.justSold.price
			);
			sellerComponent.justSold = null;
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

