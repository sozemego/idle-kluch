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
    const remainingCapacity = storage.getCapacity() - storage.getResources().length;
    if(remainingCapacity > 0 && harvesterComponent.getState() === HARVESTING_STATE.WAITING) {
      harvesterComponent.setState(HARVESTING_STATE.HARVESTING);
      harvesterComponent.setProgress(0);
    }

    if(harvesterComponent.getState() === HARVESTING_STATE.HARVESTING) {
      const unitsPerMinute = harvesterComponent.getUnitsPerMinute();
      const secondsPerUnit = 60 / unitsPerMinute;
      const harvestingProgressChange = delta / secondsPerUnit;
      const nextHarvestingPercentage = Math.min(1, harvesterComponent.getProgress() + harvestingProgressChange);
      harvesterComponent.setProgress(nextHarvestingPercentage);
    }

    if(harvesterComponent.isFinished() && harvesterComponent.getState() === HARVESTING_STATE.HARVESTING) {
      harvesterComponent.setState(HARVESTING_STATE.WAITING);
      storage.addResource(harvesterComponent.getResource());
      console.log("FINISHED HARVESTING");
    }

  };

  getEngine = () => {
    return this.engine;
  };

}

