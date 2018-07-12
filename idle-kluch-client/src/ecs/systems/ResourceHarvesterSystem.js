import Node from "../Node";
import { ResourceHarvesterComponent } from "../components/ResourceHarvesterComponent";
import { ResourceStorageComponent } from "../components/ResourceStorageComponent";
import { HARVESTING_STATE } from "../constants";
import { ResourceSourceComponent } from "../components/ResourceSourceComponent";

export class ResourceHarvesterSystem {

  constructor(engine, getResourceById) {
    this.engine = engine;
    this.node = Node.of([ ResourceHarvesterComponent, ResourceStorageComponent ]);
    this.getResourceById = getResourceById;
  }

  update = delta => {
    const entities = this.getEngine().getEntitiesByNode(this.node);
    entities.forEach(entity => this.updateEntity(entity, delta));
  };

  shouldUpdate = () => {
    return true;
  };

  updateEntity = (entity, delta) => {
    const harvesterComponent = entity.getComponent(ResourceHarvesterComponent);
    const storage = entity.getComponent(ResourceStorageComponent);

    if(harvesterComponent.getState() === HARVESTING_STATE.WAITING && harvesterComponent.getHarvests() > 0) {
      harvesterComponent.removeHarvest();
      harvesterComponent.start();
    }

    if(harvesterComponent.getState() === HARVESTING_STATE.HARVESTING) {
      const secondsPerUnit = this.getSecondsPerUnit(entity);
      const harvestingProgressChange = delta / secondsPerUnit;
      const nextHarvestingPercentage = Math.min(1, harvesterComponent.getProgress() + harvestingProgressChange);
      harvesterComponent.setProgress(nextHarvestingPercentage);
    }

    if(harvesterComponent.isFinished() && harvesterComponent.getState() === HARVESTING_STATE.HARVESTING) {
      harvesterComponent.stop();
      storage.addResource(this.getResourceById(harvesterComponent.getResource()));
    }

  };

  getSecondsPerUnit = (entity) => {
    const resourceHarvesterComponent = entity.getComponent(ResourceHarvesterComponent);
    const unitsPerMinute = resourceHarvesterComponent.getUnitsPerMinute();
    const bonus = this.getBonus(resourceHarvesterComponent.getSlots());
    const secondsPerUnit = 60 / (unitsPerMinute * bonus);
    return secondsPerUnit;
  };

  getBonus = (slots) => {
    return slots
      .map(slot => this.getEngine().getEntityById(slot.sourceId))
      .map(entity => {
        const resourceSource = entity.getComponent(ResourceSourceComponent);
        return resourceSource.getBonus();
      })
      .reduce((prev, next) => prev * next, 1);
  };

  getEngine = () => {
    return this.engine;
  };

}

