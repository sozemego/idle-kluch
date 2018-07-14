import _ from 'lodash';
import { rootSelector } from "../store/utils";

const root = rootSelector("game");

export const getTiles = (state) => root(state).tiles;
export const getSelectedEntityId = (state) => root(state).selectedEntityId;
export const getResources = (state) => root(state).resources;
export const getEngine = (state) => _.result(root(state), 'engine', null);

export const getAttachSourceSlot = (state) => root(state).attachSourceSlot;

export const getResourceByName = (state, name) => {
  const resources = getResources(state);

  return resources.find(resource => resource.name === name);
};

export const getResourceById = (state, id) => {
  const resources = getResources(state);

  return resources.find(resource => resource.resourceId === id);
};

export const getEntityById = (state, id) => {
	const engine = getEngine(state);
	if (engine) {
		return engine.getEntityById(id);
	}
};
