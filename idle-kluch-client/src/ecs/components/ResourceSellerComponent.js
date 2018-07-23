import { isEqual } from "../../utils/MathUtils";

export class ResourceSellerComponent {

	constructor(secondsPerUnit, sellingProgress, resourceBeingSold) {
		this.secondsPerUnit = secondsPerUnit;
		this.sellingProgress = sellingProgress;
		this.resourceBeingSold = resourceBeingSold;
		this.sellQueue = [];
	}

	getSecondsPerUnit() {
		return this.secondsPerUnit;
	}

	getSellingProgress() {
		return this.sellingProgress;
	}

	setSellingProgress(sellingProgress) {
		this.sellingProgress = sellingProgress;
	}

	getResourceBeingSold() {
		return this.resourceBeingSold;
	}

	setResourceBeingSold(resourceBeingSold) {
		this.resourceBeingSold = resourceBeingSold;
	}

	startSelling(resourceBeingSold) {
		this.setResourceBeingSold(resourceBeingSold);
		this.setSellingProgress(0);
	}

	stopSelling() {
		this.setResourceBeingSold(null);
		this.setSellingProgress(0);
	}

	addResourceToQueue(resource) {
		this.sellQueue.push(resource);
	}

	hasNextResourceToSell() {
		return this.sellQueue.length > 0;
	}

	getNextResourceToSell() {
		return this.sellQueue.splice(0, 1)[0] || null;
	}

	isFinished() {
		return isEqual(this.getSellingProgress(), 1, 0.01);
	}

}