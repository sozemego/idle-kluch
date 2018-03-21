import { createReducer } from "../store/utils";
import * as KINGDOM_ACTIONS from "./actions";

const initialState = {
  kingdom: null,
};

const setKingdom = (state, action) => {
  return { ...state, kingdom: action.payload };
};

const kingdom = createReducer(initialState, {
  [KINGDOM_ACTIONS.SET_KINGDOM]: setKingdom,
});

export default kingdom;