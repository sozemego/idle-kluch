import { makeActionCreator } from "../store/utils";

export const SET_KINGDOM = "SET_KINGDOM";
export const setKingdom = makeActionCreator(SET_KINGDOM, 'payload');

/**
 * Loads kingdom for the logged in user.
 * @returns {function(*, *)}
 */
export const loadKingdom = () => {
  return (dispatch, getState) => {



  };
};