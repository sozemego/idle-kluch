import { GameService as gameService } from './GameService';
import { makeActionCreator } from '../store/utils';
import createGame from './Game';
import { getConstructableBuildings } from '../kingdom/selectors';
import { setSelectedConstructableBuilding } from '../kingdom/actions';

export const ADD_TILES_TO_STATE = 'ADD_TILES_TO_STATE';
export const addTilesToState = makeActionCreator(ADD_TILES_TO_STATE, 'payload');

export const connect = () => {
	return (dispatch, getState) => {

		gameService.connect()
				.then(() => dispatch(startGame()));

	};
};

export const startGame = () => {
	return (dispatch, getState) => {

		createGame();

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