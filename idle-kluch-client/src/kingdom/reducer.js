import { createReducer } from "../store/utils";
import * as KINGDOM_ACTIONS from "./actions";

const initialState = {
  showCreateKingdomForm: false,
  kingdomNameRegistrationError: "",
  kingdom: null,
  constructableBuildings: [],
  selectedConstructableBuilding: null,
};

const setKingdom = (state, action) => {
  return { ...state, kingdom: action.payload };
};

const setKingdomRegistrationError = (state, action) => {
  return { ...state, kingdomNameRegistrationError: action.payload };
};

const setShowCreateKingdomForm = (state, action) => {
  return { ...state, showCreateKingdomForm: action.payload };
};

const setConstructableBuildings = (state, action) => {
  return { ...state, constructableBuildings: action.payload };
};

const setSelectedConstructableBuilding = (state, action) => {
  return { ...state, selectedConstructableBuilding: action.payload };
};

const kingdom = createReducer(initialState, {
  [ KINGDOM_ACTIONS.SET_KINGDOM ]: setKingdom,
  [ KINGDOM_ACTIONS.SET_KINGDOM_NAME_REGISTRATION_ERROR ]: setKingdomRegistrationError,
  [ KINGDOM_ACTIONS.SET_SHOW_CREATE_KINGDOM_FORM ]: setShowCreateKingdomForm,
  [ KINGDOM_ACTIONS.SET_CONSTRUCTABLE_BUILDINGS ]: setConstructableBuildings,
  [ KINGDOM_ACTIONS.SET_SELECTED_CONSTRUCTABLE_BUILDING ]: setSelectedConstructableBuilding,
});

export default kingdom;
