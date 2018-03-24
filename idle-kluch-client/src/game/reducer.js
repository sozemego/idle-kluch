import { createReducer } from "../store/utils";
import * as GAME_ACTIONS from './actions';

const initialState = {
  tiles: {},

};

const addTiles = (state, { payload: tiles }) => {
  const previousTiles = { ...state.tiles };
  tiles.forEach(tile => {
	const { x, y } = tile;
	previousTiles[`${x}:${y}`] = tile;
  });
  return { ...state, tiles: previousTiles };
};

const game = createReducer(initialState, {
  [GAME_ACTIONS.ADD_TILES_TO_STATE]: addTiles,
});

export default game;