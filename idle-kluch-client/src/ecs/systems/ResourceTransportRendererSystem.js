import { STORAGE_NODE } from "../nodes";
import { ResourceStorageComponent } from "../components/ResourceStorageComponent";
import { distance, getCenter } from "../utils";
import { killSprite } from "../../game/utils";

export class ResourceTransportRendererSystem {

	constructor(engine, spriteFactory, line) {
		this.engine = engine;
		this.node = STORAGE_NODE;
		this.sprites = {};
		this.spriteFactory = spriteFactory;
	}

	getEngine() {
		return this.engine;
	}

	shouldUpdate = () => true;

	update = (delta) => {
		this.getEngine()
			.getEntitiesByNode(this.node)
			.forEach(entity => this.updateEntity(entity, delta));
	};

	updateEntity = (entity, delta) => {
		const storage = entity.getComponent(ResourceStorageComponent);
		storage.getRoutes().forEach(route => {
			const target = this.getEngine().getEntityById(route.to);
			const distanceBetweenEntities = distance(entity, target);
			const distanceProgress = distanceBetweenEntities * route.progress;

			const fromCenter = getCenter(entity);
			const toCenter = getCenter(target);
			const angle = Math.atan2(toCenter.y - fromCenter.y, toCenter.x - fromCenter.x);
			const x = fromCenter.x + Math.cos(angle) * distanceProgress;
			const y = fromCenter.y + Math.sin(angle) * distanceProgress;

			const sprite = this.getSprite(route.routeId);
			sprite.width = 32;
			sprite.height = 32;
			sprite.x = x - 16;
			sprite.y = y - 16;

			if (route.finished) {
				this.destroySprite(route.routeId);
			}
		});
	}

	getSprite = (id) => {
		let sprite = this.sprites[ id ];
		if (!sprite) {
			sprite = this.spriteFactory.sprite(0, 0, "shipYellow_manned");
			sprite.autoCull = true;
			this.sprites[ id ] = sprite;
		}
		return sprite;
	};

	destroySprite = (id) => {
		const sprite = this.getSprite(id);
		killSprite(sprite);
		delete this.sprites[ id ];
	};

}