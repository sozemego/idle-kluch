import { GameService as gameService } from './GameService';
import { makeActionCreator } from '../store/utils';
import createGame from './Game';
import {events as GAME_EVENTS} from './Game'
import { getConstructableBuildings, getSelectedConstructableBuilding } from '../kingdom/selectors';
import { setSelectedConstructableBuilding } from '../kingdom/actions';
import { KingdomService as kingdomService } from "../kingdom/KingdomService";

export const ADD_TILES_TO_STATE = 'ADD_TILES_TO_STATE';
export const addTilesToState = makeActionCreator(ADD_TILES_TO_STATE, 'payload');

let game = null;

export const connect = () => {
  return (dispatch, getState) => {

	gameService.connect()
	  .then(() => dispatch(startGame()));

  };
};

export const startGame = () => {
  return (dispatch, getState) => {

	game = createGame();

  };
};

export const addTiles = (tiles) => {
  return (dispatch, getState) => {

	dispatch(addTilesToState(tiles));

  };
};

export const selectConstructableBuilding = (id) => {
  return (dispatch, getState) => {

	const constructableBuildings = getConstructableBuildings(getState);
	const building = constructableBuildings.find(building => building.id === id);
	dispatch(setSelectedConstructableBuilding(building));

  };
};

export const onCanvasClicked = (x, y) => {
  return (dispatch, getState) => {

	console.log('on canvas clicked!', x, y);

	//canvas was clicked, lets check what we can do
	const selectedConstructableBuilding = getSelectedConstructableBuilding(getState);
	if (selectedConstructableBuilding) {
	  //send network request to build

	  return kingdomService.constructBuilding(selectedConstructableBuilding.id, x, y)
		.then((building) => {
		  dispatch(setSelectedConstructableBuilding(null));
		  game.emitter.emit(GAME_EVENTS.BUILDING_PLACED, building);
		});
	}

  };
};