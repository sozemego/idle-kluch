export const makeActionCreator = (type, ...argNames) => (...args) => {
  const action = {
    type,
  };
  argNames.forEach((item, index) => {
    action[ argNames[ index ] ] = args[ index ];
  });
  return action;
};

export const makePayloadActionCreator = (type) => (arg) => {
  return {
    type,
    payload: arg,
  };
};

export const rootSelector = rootName => state => {
  return typeof state === "function" ? state()[ rootName ] : state[ rootName ];
};

export const createReducer = (initialState, handlers) => {
  return (state = initialState, action) => {
    if (handlers.hasOwnProperty(action.type)) {
      return handlers[ action.type ](state, action);
    } else {
      return state;
    }
  };
};

/**
 * Returns a function which will be able to set a given property
 * in the state to a payload in the dispatched action.
 * Simplifies creation of simple reducers which are essentially setters.
 */
export const makeSetter = (propertyName) => (state, action) => {
  return { ...state, [ propertyName ]: action.payload };
};
