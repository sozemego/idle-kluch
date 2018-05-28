import _ from "lodash";
import { createReducer, makeSetter } from "../store/utils";
import * as KINGDOM_ACTIONS from "./actions";
import * as APP_ACTIONS from "../app/actions";

const initialState = {
  showCreateKingdomForm: false,
  kingdomNameRegistrationError: "",
  kingdom: null,
  constructableBuildings: [],
  selectedConstructableBuilding: null,
  deletingKingdom: false,
};

const idleBucksChanged = (state, action) => {
  const kingdom = { ...state.kingdom };
  kingdom.idleBucks += action.payload;
  return { ...state, kingdom };
};

const kingdom = createReducer(_.cloneDeep(initialState), {
  [ KINGDOM_ACTIONS.SET_KINGDOM ]: makeSetter("kingdom"),
  [ KINGDOM_ACTIONS.SET_KINGDOM_NAME_REGISTRATION_ERROR ]: makeSetter("kingdomNameRegistrationError"),
  [ KINGDOM_ACTIONS.SET_SHOW_CREATE_KINGDOM_FORM ]: makeSetter("showCreateKingdomForm"),
  [ KINGDOM_ACTIONS.SET_CONSTRUCTABLE_BUILDINGS ]: makeSetter("constructableBuildings"),
  [ KINGDOM_ACTIONS.SET_SELECTED_CONSTRUCTABLE_BUILDING ]: makeSetter("selectedConstructableBuilding"),
  [ KINGDOM_ACTIONS.IDLE_BUCKS_CHANGED ]: idleBucksChanged,
  [ KINGDOM_ACTIONS.SET_DELETING_KINGDOM ]: makeSetter("deletingKingdom"),
  [ APP_ACTIONS.LOGOUT ]: () => _.cloneDeep(initialState),
});

export default kingdom;
