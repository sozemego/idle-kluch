import { Entity } from "./Entity";

export class EntityFactory {
  constructor(engine, componentContainer) {
    this.engine = engine;
    this.componentContainer = componentContainer;
  }

  createEntity = id => {
    return new Entity(id, this.componentContainer);
  };

  destroy = () => {
    this.engine = null;
    this.componentContainer = null;
  };

}
