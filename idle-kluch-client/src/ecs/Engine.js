import { ComponentContainer } from "./ComponentContainer";
import { EntityFactory } from "./EntityFactory";

export class Engine {
  constructor() {
    this.componentContainer = new ComponentContainer();
    this.entityFactory = new EntityFactory(this, this.componentContainer);
    this.systems = [];
    this.entities = {};
    this.addEntityQueue = {};
    this.removeEntityQueue = [];
    this.updating = false;
  }

  getEntityFactory = () => this.entityFactory;

  addSystem = system => this.systems.push(system);

  getSystem = clazz => {
    const index = this.systems.findIndex(
      system => system.constructor === clazz,
    );
    if (index > -1) {
      return this.systems[ index ];
    }

    return null;
  };

  removeSystem = system => {
    const index = this.systems.findIndex(s => s === system);
    if (index > -1) {
      this.systems.splice(index, 1);
    }
  };

  addEntity = entity => {
    if (this.entities[ entity.id ] || this.addEntityQueue[ entity.id ]) {
      throw new Error(entity.id + " entity already added");
    }

    if (!this.updating) {
      this.entities[ entity.id ] = entity;
    } else {
      this.addEntityQueue[ entity.id ] = entity;
    }
  };

  removeEntity = entityId => {
    if (!this.entities[ entityId ]) {
      throw new Error(entityId + " entity not added");
    }

    if (!this.updating) {
      //delete might be slow, profile it and check
      delete this.entities[ entityId ];
      this.componentContainer.removeEntityComponents(entityId);
    } else {
      this.removeEntityQueue.push(entityId);
    }
  };

  getAllEntities = () => {
    return Object.values(this.entities);
  };

  getEntitiesByNode = node => {
    const entityIds = this.componentContainer.getEntitiesByNode(node);

    return entityIds.map(id => this.entities[ id ]).filter(Boolean);
  };

  update = delta => {
    if (this.updating) {
      throw new Error("Engine is already updating!");
    }

    this.updating = true;

    this.systems.forEach(system => {
      if (system.shouldUpdate()) {
        system.update(delta);
      }
    });

    this.updating = false;

    const entitiesToAdd = Object.values(this.addEntityQueue);
    this.addEntityQueue = {};
    entitiesToAdd.forEach(entity => this.addEntity(entity));

    const entitiesToRemove = [ ...this.removeEntityQueue ];
    this.removeEntityQueue = [];
    entitiesToRemove.forEach(entity => this.removeEntity(entity));
  };
}
