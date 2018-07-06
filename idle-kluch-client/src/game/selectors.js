import { rootSelector } from "../store/utils";

const root = rootSelector("game");

export const getTiles = (state) => root(state).tiles;
export const getSelectedEntity = (state) => root(state).selectedEntity;
export const getResources = (state) => root(state).resources;
export const getEngine = (state) => root(state).engine;

export const getResourceByName = (state, name) => {
  const resources = getResources(state);

  return resources.find(resource => resource.name === name);
};

export const getEntityById = (state, id) => {
  return getEngine(state).getEntityById(id);
};
