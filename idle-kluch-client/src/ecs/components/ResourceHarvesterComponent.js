import { HARVESTING_STATE } from "../constants";
import { isEqual } from "../../utils/MathUtils";
export class ResourceHarvesterComponent {

  constructor(resource, radius, unitsPerMinute, slots, sourceSlots, progress = {}) {
    this.resource = resource;
    this.radius = radius;
    this.unitsPerMinute = unitsPerMinute;
    this.slots = slots;
    this.sourceSlots = sourceSlots;
    this.progress = progress.harvestingProgressPercent || 0;
    this.state = progress.harvestingState || HARVESTING_STATE.WAITING;
    this.harvests = 0;
  }

  getResource() {
    return this.resource;
  }

  setResource(resource) {
    this.resource = resource;
  }

  getRadius() {
    return this.radius;
  }

  setRadius(radius) {
    this.radius = radius;
  }

  getUnitsPerMinute() {
    return this.unitsPerMinute;
  }

  setUnitsPerMinute(unitsPerMinute) {
    this.unitsPerMinute = unitsPerMinute;
  }

  setProgress(progress) {
    this.progress = progress;
  }

  getProgress() {
    return this.progress;
  }

  setState(state) {
    this.state = state;
  }

  getState() {
    return this.state;
  }

  isFinished() {
    return isEqual(this.progress, 1, 0.01);
  }

  start() {
    this.setState(HARVESTING_STATE.HARVESTING);
    this.setProgress(0);
  }

  stop() {
    this.setState(HARVESTING_STATE.WAITING);
    this.setProgress(0);
  }

  addHarvest() {
    this.harvests++;
  }

  removeHarvest() {
    this.harvests--;
  }

  getHarvests() {
    return this.harvests;
  }

  getSlots() {
    return this.slots;
  }

  getSourceSlots() {
    return this.sourceSlots;
  }

}