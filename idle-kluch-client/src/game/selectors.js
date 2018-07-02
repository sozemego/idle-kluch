import { rootSelector } from "../store/utils";

const root = rootSelector("game");

export const getTiles = (state) => root(state).tiles;
export const getSelectedEntity = (state) => root(state).selectedEntity;
