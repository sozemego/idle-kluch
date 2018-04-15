import { rootSelector } from "../store/utils";

export const root = rootSelector("kingdom");

export const getKingdom = state => root(state).kingdom;
export const hasKingdom = state => !!getKingdom(state);
export const shouldShowCreateKingdomForm = state =>
  root(state).showCreateKingdomForm;
export const getKingdomNameRegistrationError = state =>
  root(state).kingdomNameRegistrationError;

export const getConstructableBuildings = state =>
  root(state).constructableBuildings;
export const getSelectedConstructableBuilding = state =>
  root(state).selectedConstructableBuilding;
