export class ResourceSourceComponent {

  constructor(resource, bonus) {
    this.resource = resource;
    this.bonus = bonus;
  }

  getResource() {
    return this.resource;
  }

  getBonus() {
    return this.bonus;
  }

}