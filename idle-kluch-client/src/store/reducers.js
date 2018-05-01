import { combineReducers } from "redux";

import app from "../app/reducer";
import kingdom from "../kingdom/reducer";
import { gameReducer as game } from "../game/Game";

export default combineReducers({
  app,
  kingdom,
  game,
});
