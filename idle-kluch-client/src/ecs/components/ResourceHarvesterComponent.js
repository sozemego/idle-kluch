export class ResourceHarvesterComponent {

  constructor(resource, radius, unitsPerMinute) {
    this.resource = resource;
    this.radius = radius;
    this.unitsPerMinute = unitsPerMinute;
  }

  getResource() {
    return this.resource;
  }

  setResource(resource) {
    this.resource = resource;
  }

  getRadius() {
    return this.radius;
  }

  setRadius(radius) {
    this.radius = radius;
  }

  getUnitsPerMinute() {
    return this.unitsPerMinute;
  }

  setUnitsPerMinute(unitsPerMinute) {
    this.unitsPerMinute = unitsPerMinute;
  }

}