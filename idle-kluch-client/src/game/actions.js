import { GameService as gameService } from "./GameService";
import { makeActionCreator } from "../store/utils";
import createGame from "./Game";
import {
  getConstructableBuildings,
  getSelectedConstructableBuilding
} from "../kingdom/selectors";
import { setSelectedConstructableBuilding } from "../kingdom/actions";

export const ADD_TILES = "ADD_TILES";
export const addTiles = makeActionCreator(ADD_TILES, "payload");

export const ADD_BUILDINGS = "ADD_BUILDINGS";
export const addBuildings = makeActionCreator(ADD_BUILDINGS, "payload");

export const ADD_ENTITY = "ADD_ENTITY";
export const addEntity = makeActionCreator(ADD_ENTITY, "payload");

export const connect = () => {
  return (dispatch, getState) => {
    return dispatch(startGame()).then(() => gameService.connect());
  };
};

export const startGame = () => {
  return (dispatch, getState) => {
    return createGame();
  };
};

export const selectConstructableBuilding = id => {
  return (dispatch, getState) => {
    const constructableBuildings = getConstructableBuildings(getState);
    const building = constructableBuildings.find(
      building => building.id === id
    );
    dispatch(setSelectedConstructableBuilding(building));
  };
};

export const onCanvasClicked = (x, y) => {
  return (dispatch, getState) => {
    console.log("on canvas clicked!", x, y);

    //canvas was clicked, lets check what we can do
    const selectedConstructableBuilding = getSelectedConstructableBuilding(
      getState
    );
    if (selectedConstructableBuilding) {
      //send network request to build

      gameService.constructBuilding(selectedConstructableBuilding.id, x, y);
    }
    return Promise.resolve();
  };
};
