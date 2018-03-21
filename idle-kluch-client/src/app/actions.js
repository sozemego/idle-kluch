import { makeActionCreator } from "../store/utils";
import { UserService as userService } from './UserService';
import { NetworkService as networkService } from '../api/NetworkService';
import { loadKingdom } from "../kingdom/actions";
import { isLoggedIn } from "./selectors";

export const FETCHING = 'FETCHING';
export const fetching = makeActionCreator(FETCHING);

export const STOP_FETCHING = 'STOP_FETCHING';
export const stopFetching = makeActionCreator(STOP_FETCHING);

export const SET_ERROR_MESSAGE = 'SET_ERROR_MESSAGE';
export const setErrorMessage = makeActionCreator(SET_ERROR_MESSAGE, 'payload');

export const SET_TOKEN = 'SET_TOKEN';
export const setToken = makeActionCreator(SET_TOKEN, 'payload');

export const SET_USERNAME = 'SET_USERNAME';
export const setUsername = makeActionCreator(SET_USERNAME, 'payload');

export const SET_USERNAME_ERROR = 'SET_USERNAME_ERROR';
const setUsernameError = makeActionCreator(SET_USERNAME_ERROR, 'payload');

export const SET_PASSWORD_ERROR = 'SET_PASSWORD_ERROR';
const setPasswordError = makeActionCreator(SET_PASSWORD_ERROR, 'payload');

/**
 * Function used to initialize the application.
 * @returns {function(*, *)}
 */
export const init = () => {
  return (dispatch, getState) => {

    const loggedIn = isLoggedIn(getState);

	if(loggedIn) {
      dispatch(loadKingdom());
	}

  };
};

export const register = (username, password) => {
  return (dispatch, getState) => {

	return dispatch(logout())
	  .then(() => userService.registerUser(username, password))
	  .then(() => dispatch(login(username, password)))
	  .catch(error => {
		if (error.field === 'username') {
		  return dispatch(setUsernameError(error.message));
		}
		if (error.field === 'password') {
		  return dispatch(setPasswordError(error.message));
		}
		console.log(error);
		throw error;
	  });

  };
};

export const login = (username, password) => {
  return (dispatch, getState) => {
	return userService.login(username, password)
	  .then((token) => {
		dispatch(setUsername(username));
		dispatch(setToken(token));
		dispatch(clearForms());
		networkService.setAuthorizationToken(token);
		// navigationService.mainPage();
	  })
	  .then()
	  .catch(error => dispatch(setUsernameError(error)));
  };
};

export const logout = () => {
  return (dispatch, getState) => {
	dispatch(setToken(null));
	networkService.clearAuthorizationToken();
	dispatch(setUsername('Anonymous'));
	return dispatch(clearForms());
  };
};

export const clearForms = () => {
  return (dispatch, getState) => {
	dispatch(setUsernameError(null));
	dispatch(setPasswordError(null));
	return Promise.resolve();
  };
};

export const deleteUser = () => {
  return (dispatch, getState) => {

	return userService
	  .delete()
	  .then(() => dispatch(logout()));

  };
};