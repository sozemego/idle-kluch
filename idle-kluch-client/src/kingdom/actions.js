import { makeActionCreator } from "../store/utils";
import { KingdomService as kingdomService } from "./KingdomService";
import { isDeletingKingdom } from "./selectors";

export const SET_KINGDOM = "SET_KINGDOM";
export const setKingdom = makeActionCreator(SET_KINGDOM, "payload");

export const SET_KINGDOM_NAME_REGISTRATION_ERROR = "SET_KINGDOM_NAME_REGISTRATION_ERROR";
export const setKingdomNameRegistrationError = makeActionCreator(
  SET_KINGDOM_NAME_REGISTRATION_ERROR,
  "payload",
);

export const SET_SHOW_CREATE_KINGDOM_FORM = "SET_SHOW_CREATE_KINGDOM_FORM";
export const setShowCreateKingdomForm = makeActionCreator(
  SET_SHOW_CREATE_KINGDOM_FORM,
  "payload",
);

export const SET_CONSTRUCTABLE_BUILDINGS = "SET_CONSTRUCTABLE_BUILDINGS";
export const setConstructableBuildings = makeActionCreator(
  SET_CONSTRUCTABLE_BUILDINGS,
  "payload",
);

export const SET_SELECTED_CONSTRUCTABLE_BUILDING = "SET_SELECTED_CONSTRUCTABLE_BUILDING";
export const setSelectedConstructableBuilding = makeActionCreator(
  SET_SELECTED_CONSTRUCTABLE_BUILDING,
  "payload",
);

export const IDLE_BUCKS_CHANGED = "IDLE_BUCKS_CHANGED";
export const idleBucksChanged = makeActionCreator(IDLE_BUCKS_CHANGED, "payload");

export const SET_DELETING_KINGDOM = "SET_DELETING_KINGDOM";
export const setDeletingKingdom = makeActionCreator(SET_DELETING_KINGDOM, "payload");

/**
 * Loads kingdom for the logged in user.
 */
export const loadKingdom = () => {
  return (dispatch, getState) => {
    return kingdomService
      .getOwn()
      .then(kingdom => {
        dispatch(setKingdom(kingdom));
        dispatch(setShowCreateKingdomForm(false));
      })
      .catch(error => {
        if (error.error === "User does not have a kingdom") {
          return dispatch(setShowCreateKingdomForm(true));
        }
        throw error;
      });
  };
};

export const registerKingdom = kingdomName => {
  return (dispatch, getState) => {
    return kingdomService
      .registerKingdom(kingdomName)
      .catch(error => {
        if (error.field === "name") {
          return dispatch(setKingdomNameRegistrationError(error.message));
        }
        throw error;
      })
      .then(() => dispatch(loadKingdom()));
  };
};

export const deleteKingdom = () => {
  return (dispatch, getState) => {

    if (isDeletingKingdom(getState)) {
      return Promise.resolve();
    }

    dispatch(setDeletingKingdom(true));

    return kingdomService
      .deleteKingdom()
      .then(() => {
        window.location.reload();
      })
      .catch(err => {
        window.location.reload();
      });
  };
};