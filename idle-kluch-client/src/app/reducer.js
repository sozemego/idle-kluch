import { createReducer } from '../store/utils';
import * as APP_ACTIONS from './actions';

const initialState = {
  fetchingActions: 0,
  errorMessage: '',
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

const app = createReducer(initialState, {
  [APP_ACTIONS.FETCHING]: fetching,
  [APP_ACTIONS.STOP_FETCHING]: stopFetching,
  [APP_ACTIONS.SET_ERROR_MESSAGE]: setErrorMessage,
});

export default app;