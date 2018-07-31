import { RESOURCE_SELLER_NODE } from "../nodes";
import { ResourceSellerComponent } from "../components/ResourceSellerComponent";
import { ResourceStorageComponent } from "../components/ResourceStorageComponent";

export class ResourceSellerSystem {

	constructor(engine) {
		this.engine = engine;
		this.node = RESOURCE_SELLER_NODE;
	}

	update = delta => {
		const entities = this.getEngine().getEntitiesByNode(this.node);
		entities.forEach(entity => this.updateEntity(entity, delta));
	};

	shouldUpdate = () => {
		return true;
	};

	updateEntity = (entity, delta) => {
		const sellerComponent = entity.getComponent(ResourceSellerComponent);
		const storageComponent = entity.getComponent(ResourceStorageComponent);

		const resourceBeingSold = sellerComponent.getResourceBeingSold();
		if (resourceBeingSold) {
			const secondsPerUnit = sellerComponent.getSecondsPerUnit();
			const sellingPercentageChange = delta / secondsPerUnit;
			const nextSellingPercentage = Math.min(1, sellerComponent.getSellingProgress() + sellingPercentageChange);
			sellerComponent.setSellingProgress(nextSellingPercentage);
		}

		const hasNextResourceToSell = sellerComponent.hasNextResourceToSell();
		if (!resourceBeingSold && hasNextResourceToSell) {
			const nextResourceToSell = sellerComponent.getNextResourceToSell();
			sellerComponent.startSelling(nextResourceToSell);
		}

		if (sellerComponent.isFinished()) {
			sellerComponent.stopSelling();
			sellerComponent.justSold = resourceBeingSold;
			storageComponent.removeResource(resourceBeingSold);
		}

	};

	getEngine = () => {
		return this.engine;
	};

}

