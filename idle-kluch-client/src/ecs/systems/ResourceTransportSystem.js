import { STORAGE_NODE } from "../nodes";
import { ResourceStorageComponent } from "../components/ResourceStorageComponent";
import { distance } from "../utils";

export class ResourceTransportSystem {

	constructor(engine) {
		this.engine = engine;
		this.node = STORAGE_NODE;
		this.metersPerSecond = 30;
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
			if (route.finished) {
				return storage.removeRoute(route);
			}

			const target = this.getEngine().getEntityById(route.to);
			const distanceBetweenEntities = distance(entity, target);
			const distanceChange = this.metersPerSecond * delta;
			const progressChange = distanceChange / distanceBetweenEntities;
			route.progress = Math.min(1, route.progress + progressChange);

			const targetStorage = target.getComponent(ResourceStorageComponent);
			if (route.progress >= 0.99 && targetStorage.hasRemainingCapacity()) {
				targetStorage.addResource(route.resource);
				route.finished = true;
			}

		}
	}

}