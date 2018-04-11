import { Entity } from "./Entity";

export class EntityFactory {

  constructor(engine, componentContainer) {
    this.engine = engine;
    this.componentContainer = componentContainer;
  }

  createEntity = (id) => {
    return new Entity(id, this.componentContainer);
  };

  createEntityAndAddToEngine = (id) => {
    const entity = this.createEntity(id);
    this.engine.addEntity(entity);
    return entity;
  };

}