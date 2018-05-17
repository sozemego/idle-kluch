import { rootSelector } from "../store/utils";
import { COMPONENT_TYPES } from "../game/constants";
import { findComponent } from "../ecs/utils";

export const root = rootSelector("kingdom");

export const getKingdom = state => root(state).kingdom;
export const getKingdomStartingPoint = (state) => getKingdom(state).startingPoint;
export const hasKingdom = state => !!getKingdom(state);
export const shouldShowCreateKingdomForm = state => root(state).showCreateKingdomForm;
export const getKingdomNameRegistrationError = state => root(state).kingdomNameRegistrationError;

export const getConstructableBuildingsData = state => root(state).constructableBuildings;

export const getConstructableBuildingsList = state => {
  const buildings  = getConstructableBuildingsData(state);

  return buildings.map(building => {
    const nameComponent = findComponent(building, COMPONENT_TYPES.NAME);
    const costComponent = findComponent(building, COMPONENT_TYPES.COST);
    return {
      id: building.id,
      name: nameComponent.name,
      cost: {
        idleBucks: costComponent.idleBucks,
      }
    }
  })
};

export const getSelectedConstructableBuilding = state => root(state).selectedConstructableBuilding;

export const checkCanAffordSelectedBuilding = state => {
  const kingdom = getKingdom(state);
  const { idleBucks } = kingdom;

  const selectedBuilding = getSelectedConstructableBuilding(state);
  if(!selectedBuilding) {
    return false;
  }

  const costComponent = findComponent(selectedBuilding, COMPONENT_TYPES.COST);

  return idleBucks >= costComponent.idleBucks;
}