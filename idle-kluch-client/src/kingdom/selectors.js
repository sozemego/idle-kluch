import { rootSelector } from "../store/utils";
import { COMPONENT_TYPES } from "../game/constants";
import { findComponent } from "../ecs/utils";

export const root = rootSelector("kingdom");

export const getKingdom = state => root(state).kingdom;
export const hasKingdom = state => !!getKingdom(state);
export const shouldShowCreateKingdomForm = state => root(state).showCreateKingdomForm;
export const getKingdomNameRegistrationError = state => root(state).kingdomNameRegistrationError;

export const getConstructableBuildingsData = state => root(state).constructableBuildings;

export const getConstructableBuildingsList = state => {
  const buildings  = getConstructableBuildingsData(state);

  return buildings.map(building => {
    const nameComponent = findComponent(building, COMPONENT_TYPES.NAME);
    return {
      id: building.id,
      name: nameComponent.name,
    }
  })
};

export const getSelectedConstructableBuilding = state => root(state).selectedConstructableBuilding;
