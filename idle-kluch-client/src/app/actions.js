import { makeActionCreator } from "../store/utils";
import { UserService as userService } from "./UserService";
import { NetworkService as networkService } from "../api/NetworkService";
import { loadKingdom } from "../kingdom/actions";
import { isLoggedIn } from "./selectors";

export const FETCHING = "FETCHING";
export const fetching = makeActionCreator(FETCHING);

export const STOP_FETCHING = "STOP_FETCHING";
export const stopFetching = makeActionCreator(STOP_FETCHING);

export const SET_ERROR_MESSAGE = "SET_ERROR_MESSAGE";
export const setErrorMessage = makeActionCreator(SET_ERROR_MESSAGE, "payload");

export const SET_TOKEN = "SET_TOKEN";
export const setToken = makeActionCreator(SET_TOKEN, "payload");

export const SET_USERNAME = "SET_USERNAME";
export const setUsername = makeActionCreator(SET_USERNAME, "payload");

export const SET_USERNAME_ERROR = "SET_USERNAME_ERROR";
const setUsernameError = makeActionCreator(SET_USERNAME_ERROR, "payload");

export const SET_PASSWORD_ERROR = "SET_PASSWORD_ERROR";
const setPasswordError = makeActionCreator(SET_PASSWORD_ERROR, "payload");

export const LOGOUT = "LOGOUT";
export const logout = makeActionCreator(LOGOUT);

export const LOGIN = "LOGIN";
export const login = makeActionCreator(LOGIN);

/**
 * Function used to initialize the application.
 */
export const init = () => {
  return (dispatch, getState) => {
    const loggedIn = isLoggedIn(getState);

    if (loggedIn) {
      dispatch(loadKingdom());
    }
  };
};

export const register = (username, password) => {
  return (dispatch, getState) => {
    return dispatch(logoutThunk())
      .then(() => userService.registerUser(username, password))
      .then(() => dispatch(loginThunk(username, password)))
      .catch(error => {
        if (error.field === "username") {
          return dispatch(setUsernameError(error.message));
        }
        if (error.field === "password") {
          return dispatch(setPasswordError(error.message));
        }
        console.log(error);
        throw error;
      });
  };
};

export const loginThunk = (username, password) => {
  return (dispatch, getState) => {
    return userService
      .login(username, password)
      .then(token => {
        dispatch(setUsername(username));
        dispatch(setToken(token));
        dispatch(clearForms());
        networkService.setAuthorizationToken(token);
        // navigationService.mainPage();
      })
      .then(() => dispatch(init()))
      .catch(error => dispatch(setUsernameError(error)));
  };
};

export const logoutThunk = () => {
  return (dispatch, getState) => {
    networkService.clearAuthorizationToken();
    dispatch(logout());
    return Promise.resolve();
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
    return userService.delete().then(() => dispatch(logoutThunk()));
  };
};
