import { makeActionCreator } from "../store/utils";
import { KingdomService as kingdomService } from './KingdomService';

export const SET_KINGDOM = "SET_KINGDOM";
export const setKingdom = makeActionCreator(SET_KINGDOM, 'payload');

export const SET_KINGDOM_NAME_REGISTRATION_ERROR = 'SET_KINGDOM_NAME_REGISTRATION_ERROR';
export const setKingdomNameRegistrationError = makeActionCreator(SET_KINGDOM_NAME_REGISTRATION_ERROR, 'payload');

export const SET_SHOW_CREATE_KINGDOM_FORM = 'SET_SHOW_CREATE_KINGDOM_FORM';
export const setShowCreateKingdomForm = makeActionCreator(SET_SHOW_CREATE_KINGDOM_FORM, 'payload');

/**
 * Loads kingdom for the logged in user.
 * @returns {function(*, *)}
 */
export const loadKingdom = () => {
  return (dispatch, getState) => {

	return kingdomService.getOwn()
	  .then(kingdom => {
		dispatch(setKingdom(kingdom));
		return dispatch(setShowCreateKingdomForm(false));
	  })
	  .catch(error => {
	    if(error.error === 'Kingdom not found') {
	      return dispatch(setShowCreateKingdomForm(true));
		}
		throw error;
	  });

  };
};

export const registerKingdom = (kingdomName) => {
  return (dispatch, getState) => {

	return kingdomService.registerKingdom(kingdomName)
	  .catch((error) => {
		if (error.field === 'name') {
		  return dispatch(setKingdomNameRegistrationError(error.message));
		}
		throw error;
	  })
	  .then(() => dispatch(loadKingdom()));

  };
};

export const deleteKingdom = () => {
  return (dispatch, getState) => {

	return kingdomService.deleteKingdom()
	  .then(() => dispatch(setKingdom(null)));
  };
};