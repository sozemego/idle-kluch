import { STORAGE_NODE } from "../nodes";
import { ResourceStorageComponent } from "../components/ResourceStorageComponent";
import { distance } from "../utils";

export class ResourceTransportSystem {

	constructor(engine) {
		this.engine = engine;
		this.node = STORAGE_NODE;
		this.metersPerSecond = 10;
	}

	getEngine() {
		return this.engine;
	}

	shouldUpdate = () => true;

	update = (delta) => {
		this.getEngine()
			.getEntitiesByNode(this.node)
			.forEach(entity => this.updateEntity(entity, delta));
	}

	updateEntity = (entity, delta) => {
		const storage = entity.getComponent(ResourceStorageComponent);
		if (storage.getRoutes().length > 0) {
			const route = storage.getRoutes()[0];
			const target = this.getEngine().getEntityById(route.to);
			const distanceBetweenEntities = distance(entity, target);
			const distanceChange = this.metersPerSecond * delta;
			const progressChange = distanceChange / distanceBetweenEntities;
			route.progress += progressChange;

			if (route.progress >= 0.99) {
				const targetStorage = target.getComponent(ResourceStorageComponent);
				targetStorage.addResource(route.resource);
				storage.removeRoute(route);
			}

		}
	}

}