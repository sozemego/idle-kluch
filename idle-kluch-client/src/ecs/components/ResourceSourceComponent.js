export class ResourceSourceComponent {

  constructor(resourceId, bonus) {
    this.resourceId = resourceId;
    this.bonus = bonus;
  }

  getResourceId() {
    return this.resourceId;
  }

  getBonus() {
    return this.bonus;
  }

}