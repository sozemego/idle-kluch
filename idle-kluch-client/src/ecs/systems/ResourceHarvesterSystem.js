import Node from "../Node";
import { ResourceHarvesterComponent } from "../components/ResourceHarvesterComponent";
import { ResourceStorageComponent } from "../components/ResourceStorageComponent";
import { HARVESTING_STATE } from "../constants";

export class ResourceHarvesterSystem {

  constructor(engine) {
    this.engine = engine;
    this.node = Node.of([ ResourceHarvesterComponent, ResourceStorageComponent ]);
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
      storage.addResource(harvesterComponent.getResource());
    }

  };

  getSecondsPerUnit = (entity) => {
    const resourceHarvesterComponent = entity.getComponent(ResourceHarvesterComponent);
    const unitsPerMinute = resourceHarvesterComponent.getUnitsPerMinute();
    let secondsPerUnit = 60 / unitsPerMinute;
    const sources = resourceHarvesterComponent.getSources().length;
    for (let i = 0; i < sources; i++) {
      secondsPerUnit *= 0.8;
    }
    return secondsPerUnit;
  };

  getEngine = () => {
    return this.engine;
  };

}

