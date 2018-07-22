export class ResourceSellerComponent {

	constructor(secondsPerUnit, sellingProgress, resourceBeingSold) {
		this.secondsPerUnit = secondsPerUnit;
		this.sellingProgress = sellingProgress;
		this.resourceBeingSold = resourceBeingSold;
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

}