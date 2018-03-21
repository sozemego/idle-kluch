import { createReducer } from "../store/utils";
import * as KINGDOM_ACTIONS from "./actions";

const initialState = {
  kingdomNameRegistrationError: '',
  kingdom: null,
};

const setKingdom = (state, action) => {
  return { ...state, kingdom: action.payload };
};

const setKingdomRegistrationError = (state, action) => {
  return { ...state, kingdomNameRegistrationError: action.payload };
};

const kingdom = createReducer(initialState, {
  [KINGDOM_ACTIONS.SET_KINGDOM]: setKingdom,
  [KINGDOM_ACTIONS.SET_KINGDOM_NAME_REGISTRATION_ERROR]: setKingdomRegistrationError,
});

export default kingdom;