export class Entity {
  constructor(id, componentContainer) {
    this.id = id;
    this.componentContainer = componentContainer;
  }

  addComponent = component => {
    return this.componentContainer.addComponent(this.id, component);
  };

  getComponent = clazz => {
    return this.componentContainer.getComponent(this.id, clazz);
  };

  removeComponent = clazz => {
    return this.componentContainer.removeComponent(this.id, clazz);
  };
}
