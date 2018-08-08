export class ResourceStorageComponent {

  constructor(capacity, routes, transportSpeed, transportSpeedLevel) {
    this.capacity = capacity;
    this.resources = [];
    this.routes = routes;
    this.transportSpeed = transportSpeed;
    this.transportSpeedLevel = transportSpeedLevel;
  }

  setCapacity(capacity) {
    this.capacity = capacity;
  }

  getCapacity() {
    return this.capacity;
  }

  hasRemainingCapacity() {
    return this.resources.length < this.capacity;
  }

  getResources() {
    return [...this.resources];
  }

  addResource(resource) {
    this.resources.push(resource);
  }

  removeResource(resource) {
    const index = this.resources.findIndex(r => r.id === resource.id);
    if(index > -1) {
      this.resources.splice(index, 1);
    }
  }

  addRoute(route) {
    this.routes.push(route);
  }

  getRoutes() {
    return this.routes;
  }

  removeRoute(route) {
    const index = this.routes.findIndex(r => r === route);
    if (index > -1) {
      this.routes.splice(index, 1);
    }
  }

}