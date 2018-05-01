import { rootSelector } from "../store/utils";
import { COMPONENT_TYPES } from "../game/constants";

export const root = rootSelector("kingdom");

export const getKingdom = state => root(state).kingdom;
export const hasKingdom = state => !!getKingdom(state);
export const shouldShowCreateKingdomForm = state => root(state).showCreateKingdomForm;
export const getKingdomNameRegistrationError = state => root(state).kingdomNameRegistrationError;

export const getConstructableBuildings = state => {
  const buildings  = root(state).constructableBuildings;

  return buildings.map(building => {
    const nameComponent = getNameComponent(building);
    return {
      id: building.id,
      name: nameComponent.name,
    }
  })
};

const getNameComponent = (building) => {
  return building.components.find(comp => comp.componentType === COMPONENT_TYPES.NAME);
};

export const getSelectedConstructableBuilding = state => root(state).selectedConstructableBuilding;
