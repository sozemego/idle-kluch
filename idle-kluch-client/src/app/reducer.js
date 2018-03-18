import { createReducer } from '../store/utils';
import * as APP_ACTIONS from './actions';
import { NetworkService as networkService } from '../api/NetworkService';

const anonymousUser = {
  name: null,
  token: null,
};

//TODO abstraction over localStorage
const _getCurrentUser = () => {
  const name = localStorage.getItem('username');
  const token = localStorage.getItem('jwt');

  if (!name || !token) {
	return anonymousUser;
  }

  networkService.setAuthorizationToken(token);
  return { name, token };
};

const initialState = {
  fetchingActions: 0,
  errorMessage: '',
  user: _getCurrentUser(),
  usernameError: '',
  passwordError: '',
};

const fetching = (state, action) => {
  let fetchingActions = state.fetchingActions;
  return { ...state, fetchingActions: ++fetchingActions };
};

const stopFetching = (state, action) => {
  let fetchingActions = state.fetchingActions;
  return { ...state, fetchingActions: --fetchingActions };
};

const setErrorMessage = (state, action) => {
  return { ...state, errorMessage: action.payload };
};

const setUsername = (state, action) => {
  action.payload ? localStorage.setItem('username', action.payload) : localStorage.removeItem('username');
  return { ...state, currentUser: { ...state.currentUser, name: action.username } };
};

const setToken = (state, action) => {
  action.payload ? localStorage.setItem('jwt', action.payload) : localStorage.removeItem('jwt');
  return { ...state, currentUser: { ...state.currentUser, token: action.token } };
};

const setUsernameError = (state, action) => {
  return { ...state, usernameError: action.payload };
};

const setPasswordError = (state, action) => {
  return { ...state, passwordError: action.payload };
};

const app = createReducer(initialState, {
  [APP_ACTIONS.FETCHING]: fetching,
  [APP_ACTIONS.STOP_FETCHING]: stopFetching,
  [APP_ACTIONS.SET_ERROR_MESSAGE]: setErrorMessage,
  [APP_ACTIONS.SET_USERNAME]: setUsername,
  [APP_ACTIONS.SET_TOKEN]: setToken,
  [APP_ACTIONS.SET_USERNAME_ERROR]: setUsernameError,
  [APP_ACTIONS.SET_PASSWORD_ERROR]: setPasswordError,
});

export default app;