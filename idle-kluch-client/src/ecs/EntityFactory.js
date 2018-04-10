import { Entity } from "./Entity";

export class EntityFactory {

  constructor(engine, componentContainer) {
    this.engine = engine;
    this.componentContainer = componentContainer;
    this.id = 0;
  }

  createEntity = () => {
    return new Entity(++this.id, this.componentContainer);
  };

  createEntityAndAddToEngine = () => {
    const entity = this.createEntity();
    this.engine.addEntity(entity);
    return entity;
  };

}