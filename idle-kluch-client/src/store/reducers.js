import { combineReducers } from "redux";

import app from "../app/reducer";
import kingdom from "../kingdom/reducer";
import game from "../game/reducer";

export default combineReducers({
	app,
	kingdom,
	game,
});