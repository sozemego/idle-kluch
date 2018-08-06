import _ from "lodash";
import { createReducer, makeSetter } from "../store/utils";
import * as KINGDOM_ACTIONS from "./actions";
import * as APP_ACTIONS from "../app/actions";
// import * as GAME_ACTIONS from "../game/actions";
import { findComponent } from "../ecs/utils";
import { COMPONENT_TYPES } from "../game/constants";

const initialState = {
  showCreateKingdomForm: false,
  kingdomNameRegistrationError: "",
  kingdom: null,
  constructableBuildings: [],
  selectedConstructableBuilding: null,
  deletingKingdom: false,
  ownBuildings: {},
  cashHistory: [],
	upgrades: {},
};

const addEntity = (state, action) => {
  const entity = action.payload;

  const nameComponent = findComponent(entity, COMPONENT_TYPES.NAME);
  if (!nameComponent) {
    return state;
  }

  const ownershipComponent = findComponent(entity, COMPONENT_TYPES.OWNERSHIP);
  if (!ownershipComponent) {
    return state;
  }

  const buildable = findComponent(entity, COMPONENT_TYPES.BUILDABLE);
  if (!buildable) {
    return state;
  }

  if (ownershipComponent.ownerId !== state.kingdom.id) {
    return state;
  }

  const ownBuildings = {...state.ownBuildings};
  const number = ownBuildings[nameComponent.name] || 0;
	ownBuildings[nameComponent.name] = number + 1;

	return {...state, ownBuildings};
};

const idleBucksChanged = (state, action) => {
	const kingdom = { ...state.kingdom };
	kingdom.idleBucks += action.payload;

	let cashHistory = [...state.cashHistory];
	if (action.payload > 0) {
		const now = Date.now();
		const tenSeconds = 10 * 1000;
		cashHistory = cashHistory.filter(event => event.time > now - tenSeconds);
		cashHistory.push({time: now, cash: action.payload });
  }
	return { ...state, kingdom, cashHistory };
};

const setUpgrades = (state, action) => {
	return { ...state, upgrades: action.payload };
}

const kingdom = createReducer(_.cloneDeep(initialState), {
  [ KINGDOM_ACTIONS.SET_KINGDOM ]: makeSetter("kingdom"),
  [ KINGDOM_ACTIONS.SET_KINGDOM_NAME_REGISTRATION_ERROR ]: makeSetter("kingdomNameRegistrationError"),
  [ KINGDOM_ACTIONS.SET_SHOW_CREATE_KINGDOM_FORM ]: makeSetter("showCreateKingdomForm"),
  [ KINGDOM_ACTIONS.SET_CONSTRUCTABLE_BUILDINGS ]: makeSetter("constructableBuildings"),
  [ KINGDOM_ACTIONS.SET_SELECTED_CONSTRUCTABLE_BUILDING ]: makeSetter("selectedConstructableBuilding"),
  [ KINGDOM_ACTIONS.IDLE_BUCKS_CHANGED ]: idleBucksChanged,
  [ KINGDOM_ACTIONS.SET_DELETING_KINGDOM ]: makeSetter("deletingKingdom"),
	[ KINGDOM_ACTIONS.SET_UPGRADES ]: setUpgrades,
  //TODO fix this, extract actions
	[ 'ADD_ENTITY' ]: addEntity,
  [ APP_ACTIONS.LOGOUT ]: () => _.cloneDeep(initialState),
});

export default kingdom;
