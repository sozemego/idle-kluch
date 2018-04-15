export class EntitySystem {
  constructor(engine) {
    this.engine = engine;
  }

  shouldUpdate = () => true;

  getEngine = () => this.engine;

  update = delta => {};
}
