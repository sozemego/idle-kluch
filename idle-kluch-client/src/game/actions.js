import { GameService as gameService } from './GameService';
import { makeActionCreator } from "../store/utils";
import createGame from './Game';

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