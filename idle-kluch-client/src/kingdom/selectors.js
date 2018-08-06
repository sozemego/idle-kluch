import { rootSelector } from "../store/utils";
import { COMPONENT_TYPES } from "../game/constants";
import { findComponent } from "../ecs/utils";

export const root = rootSelector("kingdom");

export const getKingdom = state => root(state).kingdom;
export const getKingdomStartingPoint = (state) => getKingdom(state).startingPoint;
export const hasKingdom = state => !!getKingdom(state);
export const shouldShowCreateKingdomForm = state => root(state).showCreateKingdomForm;
export const getKingdomNameRegistrationError = state => root(state).kingdomNameRegistrationError;

export const isDeletingKingdom = state => root(state).deletingKingdom;

export const getConstructableBuildingsData = state => root(state).constructableBuildings;
export const getOwnBuildings = (state) => root(state).ownBuildings;

export const getConstructableBuildingsList = state => {
  const buildings  = getConstructableBuildingsData(state);

  return buildings.map(building => {
    const nameComponent = findComponent(building, COMPONENT_TYPES.NAME);
    return {
      id: building.id,
      name: nameComponent.name,
      cost: {
        idleBucks: getCost(state, building),
      }
    }
  })
};

const getMultiplier = (name, ownBuildings) => {
  if (!ownBuildings[name]) {
    return 1;
  }

  let result = 1;
  for (let i = 0; i < ownBuildings[name]; i++) {
    result = result * (1.1 + (ownBuildings[name] * 0.030));
  }
  return result;
};

export const getCostMultiplier = (state, name) => {
	const ownBuildings = getOwnBuildings(state);
	return getMultiplier(name, ownBuildings);
};

export const getCost = (state, building) => {
  const baseCost = findComponent(building, COMPONENT_TYPES.COST).idleBucks;
  const name = findComponent(building, COMPONENT_TYPES.NAME).name;
	const ownBuildings = getOwnBuildings(state);

	return Number(Number(baseCost * getMultiplier(name, ownBuildings)).toFixed(0));
};

export const getSelectedConstructableBuilding = state => root(state).selectedConstructableBuilding;

export const checkCanAffordSelectedBuilding = state => {
  const kingdom = getKingdom(state);
  if(!kingdom) {
    return false;
  }
  const { idleBucks } = kingdom;

  const selectedBuilding = getSelectedConstructableBuilding(state);
  if(!selectedBuilding) {
    return false;
  }

  return idleBucks >= getCost(state, selectedBuilding);
};

export const getCashHistory = (state) => {
	const cashHistory = root(state).cashHistory;
	const now = Date.now();
	const tenSeconds = 10 * 1000;
  return cashHistory.filter(event => {
    return event.time > (now - tenSeconds);
  });
};

export const getUpgrades = (state) => root(state).upgrades;