import { combineReducers } from 'redux';

import app from '../app/reducer';
import kingdom from '../kingdom/reducer';

export default combineReducers({
  app,
  kingdom,
});