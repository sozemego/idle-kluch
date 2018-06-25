export class ResourceStorageComponent {

  constructor(capacity) {
    this.capacity = capacity;
    this.resources = [];
  }

  setCapacity(capacity) {
    this.capacity = capacity;
  }

  getCapacity() {
    return this.capacity;
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

}